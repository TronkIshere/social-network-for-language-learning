package com.private_project.charitable_money_management.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.private_project.charitable_money_management.configuration.UserPrincipal;
import com.private_project.charitable_money_management.dto.request.authentication.LogoutRequest;
import com.private_project.charitable_money_management.dto.request.authentication.SignInRequest;
import com.private_project.charitable_money_management.dto.response.authentication.RefreshTokenResponse;
import com.private_project.charitable_money_management.dto.response.authentication.SignInResponse;
import com.private_project.charitable_money_management.dto.response.authentication.SignInStatus;
import com.private_project.charitable_money_management.entity.AuthProvider;
import com.private_project.charitable_money_management.entity.User;
import com.private_project.charitable_money_management.exception.ApplicationException;
import com.private_project.charitable_money_management.exception.ErrorCode;
import com.private_project.charitable_money_management.repository.UserRepository;
import com.private_project.charitable_money_management.service.AuthenticationService;
import com.private_project.charitable_money_management.service.JwtService;
import com.private_project.charitable_money_management.service.RedisService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    UserDetailsServiceCustomizer userDetailsServiceCustomizer;
    AuthenticationManager authenticationManager;
    UserRepository userRepository;
    RedisService redisService;
    JwtService jwtService;

    @Override
    public SignInResponse signIn(SignInRequest request, HttpServletResponse response) {
        UserPrincipal userPrincipal = authenticateAndGetUserPrincipal(request.getEmail(), request.getPassword());
        User user = getUserById(userPrincipal.getId());

        return generateTokenResponse(userPrincipal, user, response);
    }

    private UserPrincipal authenticateAndGetUserPrincipal(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.EMAIL_NOT_FOUND));

        boolean hasLocalProvider = user.getUserProviders().stream()
                .anyMatch(up -> up.getProvider() == AuthProvider.LOCAL);

        if (!hasLocalProvider) {
            throw new ApplicationException(ErrorCode.ACCOUNT_NOT_LINKED_WITH_LOCAL);
        }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return (UserPrincipal) authentication.getPrincipal();
    }

    private User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private SignInResponse generateTokenResponse(UserPrincipal userPrincipal, User user, HttpServletResponse response) {
        final String accessToken = jwtService.generateAccessToken(userPrincipal);
        final String refreshToken = jwtService.generateRefreshToken(userPrincipal);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        Cookie cookie = createCookie(refreshToken);
        response.addCookie(cookie);

        return SignInResponse.builder()
                .status(SignInStatus.SUCCESS)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }

    private Cookie createCookie(String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(14 * 24 * 60 * 60);
        return cookie;
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) throws ParseException, JOSEException {
        log.info("refresh token");

        validateRefreshToken(refreshToken);
        String email = jwtService.extractUserName(refreshToken);
        UserPrincipal userDetails = userDetailsServiceCustomizer.loadUserByUsername(email);

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userDetails.getId()));

        isValidToken(user, refreshToken);
        String accessToken = jwtService.generateAccessToken(UserPrincipal.create(user));

        log.info("refresh token success");
        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .userId(user.getId())
                .build();
    }

    private void validateRefreshToken(String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            log.warn("Empty refresh token provided");
            throw new ApplicationException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    private void isValidToken(User user, String refreshToken) throws ParseException, JOSEException {
        log.debug("Comparing tokens - DB: {} vs Input: {}", user.getRefreshToken(), refreshToken);

        if(StringUtils.isBlank(user.getRefreshToken())) {
            log.error("Refresh token in DB is empty for user: {}", user.getId());
            throw new ApplicationException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        if(!Objects.equals(refreshToken, user.getRefreshToken())) {
            log.error("Refresh token mismatch for user: {}", user.getId());
            throw new ApplicationException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        if(!jwtService.verificationToken(refreshToken, UserPrincipal.create(user))) {
            log.error("JWT verification failed for token: {}", refreshToken);
            throw new ApplicationException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    @Override
    public void signOut(final LogoutRequest request, final HttpServletResponse response) {
        final String email = jwtService.extractUserName(request.getAccessToken());
        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_EXISTED));

        final long accessTokenExp = jwtService.extractTokenExpired(request.getAccessToken());
        if (accessTokenExp <= 0) {
            return;
        }

        try {
            final String jwtId = SignedJWT.parse(request.getAccessToken())
                    .getJWTClaimsSet().getJWTID();
            redisService.save(jwtId, request.getAccessToken(), accessTokenExp, TimeUnit.MILLISECONDS);

            user.setRefreshToken(null);
            userRepository.save(user);

            deleteRefreshTokenCookie(response);
        } catch (ParseException e) {
            throw new ApplicationException(ErrorCode.SIGN_OUT_FAILED);
        }
    }

    private void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
