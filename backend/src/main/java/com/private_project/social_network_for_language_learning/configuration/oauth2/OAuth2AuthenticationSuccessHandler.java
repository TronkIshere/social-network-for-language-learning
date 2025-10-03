package com.private_project.social_network_for_language_learning.configuration.oauth2;

import com.private_project.social_network_for_language_learning.configuration.UserPrincipal;
import com.private_project.social_network_for_language_learning.dto.response.authentication.SignInResponse;
import com.private_project.social_network_for_language_learning.dto.response.authentication.SignInStatus;
import com.private_project.social_network_for_language_learning.dto.response.common.ResponseAPI;
import com.private_project.social_network_for_language_learning.entity.User;
import com.private_project.social_network_for_language_learning.exception.ApplicationException;
import com.private_project.social_network_for_language_learning.exception.ErrorCode;
import com.private_project.social_network_for_language_learning.repository.UserRepository;
import com.private_project.social_network_for_language_learning.service.JwtService;
import com.private_project.social_network_for_language_learning.util.CookieUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static com.private_project.social_network_for_language_learning.configuration.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    JwtService jwtService;
    UserRepository userRepository;
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    ObjectMapper objectMapper;

    @Value("#{'${security.jwt.oauth2.authorized-redirect-uris:}'.split(',')}")
    private List<String> authorizedRedirectUris;

    @PostConstruct
    public void init() {
        log.info("Loaded redirect URIs: {}", authorizedRedirectUris);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            handleSuccessfulAuthentication(request, response, authentication);
        } catch (UsernameNotFoundException e) {
            handleUserNotFoundException(response, e);
        } catch (Exception e) {
            handleAuthenticationFailure(response, e);
        }
    }

    private void handleSuccessfulAuthentication(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException {
        clearAuthenticationAttributes(request, response);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);
        saveRefreshToken(userPrincipal.getId(), refreshToken);

        SignInResponse signInResponse = buildSignInResponse(userPrincipal, accessToken, refreshToken);
        ResponseAPI<SignInResponse> successResponse = buildSuccessResponse(signInResponse);

        sendJsonResponse(response, HttpStatus.OK, successResponse);
    }

    private void saveRefreshToken(UUID userId, String refreshToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_EXISTED));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    private SignInResponse buildSignInResponse(UserPrincipal userPrincipal, String accessToken, String refreshToken) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow();

        return SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .status(SignInStatus.SUCCESS)
                .userId(user.getId())
                .build();
    }

    private ResponseAPI<SignInResponse> buildSuccessResponse(SignInResponse signInResponse) {
        return ResponseAPI.<SignInResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Authentication successful")
                .data(signInResponse)
                .build();
    }

    private void handleUserNotFoundException(HttpServletResponse response, UsernameNotFoundException e) throws IOException {
        log.error("User not found after OAuth2 authentication", e);
        ResponseAPI<Void> errorResponse = buildErrorResponse(HttpStatus.NOT_FOUND, "User account not found");
        sendJsonResponse(response, HttpStatus.NOT_FOUND, errorResponse);
    }

    private void handleAuthenticationFailure(HttpServletResponse response, Exception e) throws IOException {
        log.error("OAuth2 authentication processing failed", e);
        ResponseAPI<Void> errorResponse = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Authentication failed");
        sendJsonResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, errorResponse);
    }

    private ResponseAPI<Void> buildErrorResponse(HttpStatus status, String message) {
        return ResponseAPI.<Void>builder()
                .code(status.value())
                .message(message)
                .build();
    }

    private void sendJsonResponse(HttpServletResponse response, HttpStatus status, Object body) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        objectMapper.writeValue(response.getWriter(), body);
    }

    public String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            try {
                throw new BadRequestException("Unauthorized Redirect URI");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateAccessToken(userPrincipal);

        response.addHeader("Authorization", "Bearer " + token);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        if (uri == null || uri.isBlank()) {
            log.warn("Redirect URI is empty");
            return false;
        }

        if (authorizedRedirectUris == null || authorizedRedirectUris .isEmpty()) {
            log.warn("No authorized redirect URIs configured");
            return false;
        }

        try {
            URI clientUri = URI.create(uri.split("#")[0]).normalize();

            return authorizedRedirectUris.stream()
                    .filter(Objects::nonNull)
                    .filter(Predicate.not(String::isBlank))
                    .map(this::createNormalizedUri)
                    .filter(Objects::nonNull)
                    .anyMatch(configuredUri -> matchesUri(configuredUri, clientUri));
        } catch (IllegalArgumentException e) {
            log.error("Invalid redirect URI format: {}", uri, e);
            return false;
        }
    }

    private URI createNormalizedUri(String uri) {
        try {
            return URI.create(uri.split("#")[0]).normalize();
        } catch (IllegalArgumentException e) {
            log.warn("Invalid authorized URI in config: {}", uri);
            return null;
        }
    }

    private boolean matchesUri(URI configuredUri, URI clientUri) {
        return configuredUri.getScheme().equalsIgnoreCase(clientUri.getScheme())
                && configuredUri.getHost().equalsIgnoreCase(clientUri.getHost())
                && configuredUri.getPort() == clientUri.getPort();
    }
}
