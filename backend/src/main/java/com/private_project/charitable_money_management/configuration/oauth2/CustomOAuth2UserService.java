package com.private_project.charitable_money_management.configuration.oauth2;

import com.private_project.charitable_money_management.configuration.UserPrincipal;
import com.private_project.charitable_money_management.configuration.oauth2.user.OAuth2UserInfo;
import com.private_project.charitable_money_management.configuration.oauth2.user.OAuth2UserInfoFactory;
import com.private_project.charitable_money_management.entity.AuthProvider;
import com.private_project.charitable_money_management.entity.Role;
import com.private_project.charitable_money_management.entity.User;
import com.private_project.charitable_money_management.entity.UserProvider;
import com.private_project.charitable_money_management.exception.OAuth2AuthenticationProcessingException;
import com.private_project.charitable_money_management.repository.RoleRepository;
import com.private_project.charitable_money_management.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    RoleRepository roleRepository;
    UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        if (oAuth2UserRequest.getClientRegistration() == null) {
            throw new OAuth2AuthenticationProcessingException("Invalid OAuth2 configuration: ClientRegistration is null");
        }

        try {
            OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
            log.info("OAuth2 User Attributes: {}", oAuth2User.getAttributes());
            log.info("OAuth2 User Name: {}", oAuth2User.getName());

            if (oAuth2User.getAttributes() == null || oAuth2User.getAttributes().isEmpty()) {
                throw new OAuth2AuthenticationProcessingException("OAuth2 attributes are empty");
            }

            return processOAuth2User(oAuth2UserRequest, oAuth2User);

        } catch (OAuth2AuthenticationException ex) {
            throw new OAuth2AuthenticationProcessingException("OAuth2 authentication failed", ex);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oauth2Request, OAuth2User oauth2User) {
        OAuth2UserInfo oauth2UserInfo = getOAuth2UserInfo(oauth2Request, oauth2User);
        validateOAuth2UserInfo(oauth2UserInfo);

        User user = userRepository.findByEmailWithProviders(oauth2UserInfo.getEmail())
                .map(existingUser -> handleExistingUser(existingUser, oauth2Request, oauth2UserInfo))
                .orElseGet(() -> registerNewUser(oauth2Request, oauth2UserInfo));

        return UserPrincipal.create(user, oauth2User.getAttributes());
    }

    private OAuth2UserInfo getOAuth2UserInfo(OAuth2UserRequest request, OAuth2User oauth2User) {
        String providerId = request.getClientRegistration().getRegistrationId();
        return OAuth2UserInfoFactory.getOAuth2UserInfo(providerId, oauth2User.getAttributes());
    }

    private void validateOAuth2UserInfo(OAuth2UserInfo oauth2UserInfo) {
        if (!StringUtils.hasText(oauth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
    }

    private User handleExistingUser(User existingUser,
                                    OAuth2UserRequest request,
                                    OAuth2UserInfo oauth2UserInfo) {
        AuthProvider provider = getProvider(request);

        boolean hasValidProvider = existingUser.getUserProviders().stream()
                .anyMatch(userProvider -> userProvider.getProvider() == provider);

        if (!hasValidProvider) {
            return addProvider(existingUser, provider, oauth2UserInfo);
        } else {
            return updateExistingUser(existingUser, oauth2UserInfo);
        }
    }

    private User addProvider(User existingUser, AuthProvider provider, OAuth2UserInfo oAuth2UserInfo) {
        UserProvider userProvider = new UserProvider();
        userProvider.setProvider(provider);
        userProvider.setProviderId(oAuth2UserInfo.getId());
        userProvider.setUser(existingUser);
        existingUser.getUserProviders().add(userProvider);

        return userRepository.save(existingUser);
    }

    private AuthProvider getProvider(OAuth2UserRequest request) {
        return AuthProvider.valueOf(
                request.getClientRegistration().getRegistrationId().toUpperCase()
        );
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());

        AuthProvider provider;
        try {
            String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase();
            provider = AuthProvider.valueOf(registrationId);
        } catch (IllegalArgumentException e) {
            throw new OAuth2AuthenticationProcessingException("Invalid provider: " +
                    oAuth2UserRequest.getClientRegistration().getRegistrationId());
        }

        UserProvider userProvider = new UserProvider();
        userProvider.setProvider(provider);
        userProvider.setProviderId(oAuth2UserInfo.getId());
        userProvider.setUser(user);

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Collections.singleton(role));
        user.setUserProviders(new ArrayList<>(List.of(userProvider)));

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        return userRepository.save(existingUser);
    }

}
