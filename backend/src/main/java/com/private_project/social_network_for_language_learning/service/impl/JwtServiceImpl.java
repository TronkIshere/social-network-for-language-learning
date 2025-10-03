package com.private_project.social_network_for_language_learning.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.private_project.social_network_for_language_learning.configuration.UserPrincipal;
import com.private_project.social_network_for_language_learning.entity.Role;
import com.private_project.social_network_for_language_learning.entity.User;
import com.private_project.social_network_for_language_learning.exception.ApplicationException;
import com.private_project.social_network_for_language_learning.exception.ErrorCode;
import com.private_project.social_network_for_language_learning.repository.UserRepository;
import com.private_project.social_network_for_language_learning.service.JwtService;
import com.private_project.social_network_for_language_learning.service.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "JWT-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtServiceImpl implements JwtService {
    @Value("${spring.security.jwt.secret}")
    String jwtSecret;
    final RedisService redisService;
    final UserRepository userRepository;

    @Override
    public String generateAccessToken(UserPrincipal user) {
        byte[] secretKeyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("identity-service")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(60, ChronoUnit.MINUTES).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("roles", buildRoles(user))
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(secretKeyBytes));
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to sign JWT", e);
        }

        return jwsObject.serialize();
    }

    private List<String> buildRoles(UserPrincipal userPrincipal) {
        if (userPrincipal == null || userPrincipal.getRoles() == null) {
            throw new ApplicationException(ErrorCode.USER_NOT_EXISTED);
        }

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    @Override
    public boolean verificationToken(String token, UserPrincipal user) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        var jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        if(StringUtils.isNotBlank(redisService.get(jwtId))) {
            throw new ApplicationException(ErrorCode.TOKEN_BLACKLISTED);
        }
        var email = signedJWT.getJWTClaimsSet().getSubject();
        var expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        if( !Objects.equals(email, user.getEmail())) {
            log.error("Email in token not match email system");
            throw new ApplicationException(ErrorCode.TOKEN_INVALID);
        }
        if(expiration.before(new Date())) {
            log.error("Token expired");
            throw new ApplicationException(ErrorCode.TOKEN_INVALID);
        }

        return signedJWT.verify(new MACVerifier(jwtSecret));
    }

    @Override
    public String extractUserName(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new ApplicationException(ErrorCode.TOKEN_INVALID);
        }
    }

    @Override
    public String generateRefreshToken(UserPrincipal user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        var claimsSet =  new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("identity-service")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(14, ChronoUnit.DAYS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();

        var payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(jwtSecret));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    @Override
    public long extractTokenExpired(String token) {
        try {
            long expirationTime = SignedJWT.parse(token)
                    .getJWTClaimsSet().getExpirationTime().getTime();
            long currentTime = System.currentTimeMillis();
            return Math.max(expirationTime - currentTime, 0);
        } catch (ParseException e) {
            throw new ApplicationException(ErrorCode.TOKEN_INVALID);
        }
    }

    @Override
    public String getUserEmailFromToken(String token) {
        if (token == null || token.isBlank()) {
            log.error("Token is null or empty");
            throw new ApplicationException(ErrorCode.TOKEN_INVALID);
        }

        try {
            String tokenSecret = jwtSecret;

            if (tokenSecret == null || tokenSecret.isBlank()) {
                log.error("JWT secret is not configured");
                throw new ApplicationException(ErrorCode.JWT_SECRET_NOT_CONFIGURED);
            }

            Key signingKey = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String subject = claims.getSubject();
            if (subject == null || subject.isBlank()) {
                log.error("Token subject is empty");
                throw new ApplicationException(ErrorCode.TOKEN_INVALID);
            }

            try {
                return subject;
            } catch (IllegalArgumentException e) {
                log.error("Invalid user ID format in token", e);
                throw new ApplicationException(ErrorCode.TOKEN_INVALID);
            }

        } catch (ExpiredJwtException e) {
            log.error("JWT token expired", e);
            throw new ApplicationException(ErrorCode.TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token format", e);
            throw new ApplicationException(ErrorCode.TOKEN_INVALID);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
            throw new ApplicationException(ErrorCode.TOKEN_INVALID);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Failed to parse JWT token", e);
            throw new ApplicationException(ErrorCode.TOKEN_INVALID);
        }
    }

    @Override
    public boolean validateToken(String authToken) {
        try {
            String tokenSecret = jwtSecret;

            if (tokenSecret == null || tokenSecret.trim().isEmpty()) {
                log.error("Token secret is null or empty");
                return false;
            }

            Key signingKey = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));

            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(authToken);

            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token", ex);
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token", ex);
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.", ex);
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature", ex);
        }
        return false;
    }

    @Override
    public String getEmailFromToken(String token) {
        try {
            String tokenSecret = jwtSecret;
            Key signingKey = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            if (email == null || email.isBlank()) {
                throw new ApplicationException(ErrorCode.TOKEN_INVALID);
            }

            return email;

        } catch (ExpiredJwtException e) {
            throw new ApplicationException(ErrorCode.TOKEN_EXPIRED);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.TOKEN_INVALID);
        }
    }

    @Override
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (org.springframework.util.StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
