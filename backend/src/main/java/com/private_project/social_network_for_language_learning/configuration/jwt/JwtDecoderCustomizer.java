package com.private_project.social_network_for_language_learning.configuration.jwt;

import com.private_project.social_network_for_language_learning.configuration.UserPrincipal;
import com.private_project.social_network_for_language_learning.entity.User;
import com.private_project.social_network_for_language_learning.exception.ApplicationException;
import com.private_project.social_network_for_language_learning.exception.ErrorCode;
import com.private_project.social_network_for_language_learning.repository.UserProviderRepository;
import com.private_project.social_network_for_language_learning.repository.UserRepository;
import com.private_project.social_network_for_language_learning.service.JwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import io.jsonwebtoken.JwtException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "JWT-DECODER")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtDecoderCustomizer implements JwtDecoder {
    @Value("${security.jwt.secret}")
    String secretKey;
    final UserProviderRepository userProviderRepository;
    final UserRepository userRepository;
    final JwtService jwtService;
    NimbusJwtDecoder nimbusJwtDecoder;

    @Override
    public Jwt decode(String token) throws JwtException {
        initializeNimbusJwtDecoder();
        User user = findUserByToken(token);
        validateToken(token, user);
        return decodeToken(token);
    }

    private void initializeNimbusJwtDecoder() {
        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKey key = new SecretKeySpec(secretKey.getBytes(), JWSAlgorithm.HS512.toString());
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(key)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
    }

    private User findUserByToken(String token) {
        String email = jwtService.extractUserName(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_EXISTED));

        user.setUserProviders(userProviderRepository.findUserProvidersByUserEmail(email));

        return user;
    }

    private void validateToken(String token, User user) {
        try {
            boolean isValid = jwtService.verificationToken(token, UserPrincipal.create(user));
            if (!isValid) {
                throw new JwtException("Invalid token");
            }
        } catch (ParseException | JOSEException e) {
            log.error("Jwt decoder: Token invalid", e);
            throw new ApplicationException(ErrorCode.TOKEN_INVALID);
        }
    }

    private Jwt decodeToken(String token) {
        return nimbusJwtDecoder.decode(token);
    }

}
