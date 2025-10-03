package com.private_project.charitable_money_management.service;

import com.nimbusds.jose.JOSEException;
import com.private_project.charitable_money_management.configuration.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;

import java.text.ParseException;

public interface JwtService {
    String generateAccessToken(UserPrincipal user);

    boolean verificationToken(String token, UserPrincipal user) throws ParseException, JOSEException;

    String extractUserName(String token);

    String generateRefreshToken(UserPrincipal user);

    long extractTokenExpired(String accessToken);

    String getUserEmailFromToken(String token);

    boolean validateToken(String authToken);

    String getEmailFromToken(String jwt);

    String getJwtFromRequest(HttpServletRequest request);
}
