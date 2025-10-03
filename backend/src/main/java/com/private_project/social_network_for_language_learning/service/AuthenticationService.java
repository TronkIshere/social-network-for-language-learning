package com.private_project.social_network_for_language_learning.service;

import com.nimbusds.jose.JOSEException;
import com.private_project.social_network_for_language_learning.dto.request.authentication.LogoutRequest;
import com.private_project.social_network_for_language_learning.dto.request.authentication.SignInRequest;
import com.private_project.social_network_for_language_learning.dto.response.authentication.RefreshTokenResponse;
import com.private_project.social_network_for_language_learning.dto.response.authentication.SignInResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;

import java.text.ParseException;

public interface AuthenticationService {
    SignInResponse signIn(SignInRequest request, HttpServletResponse response);

    RefreshTokenResponse refreshToken(@CookieValue(name = "refreshToken") String refreshToken) throws ParseException, JOSEException;

    void signOut(LogoutRequest request, HttpServletResponse response);
}

