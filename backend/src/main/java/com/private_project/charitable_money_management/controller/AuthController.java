package com.private_project.charitable_money_management.controller;

import com.private_project.charitable_money_management.dto.request.authentication.LogoutRequest;
import com.private_project.charitable_money_management.dto.request.authentication.SignInRequest;
import com.private_project.charitable_money_management.dto.response.authentication.RefreshTokenResponse;
import com.private_project.charitable_money_management.dto.response.authentication.SignInResponse;
import com.private_project.charitable_money_management.dto.response.common.ResponseAPI;
import com.private_project.charitable_money_management.service.AuthenticationService;
import com.private_project.charitable_money_management.util.SignOnUtils;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@FieldDefaults(level = AccessLevel.PUBLIC)
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    ResponseAPI<SignInResponse> authenticateUser(@Valid @RequestBody SignInRequest request, HttpServletResponse response) {
        var result = authenticationService.signIn(request, response);
        SignOnUtils.set(new SignOnUtils.SignOnUser(
                result.getUserId(),
                result.getAccessToken(),
                result.getRefreshToken(),
                result.getStatus(),
                result.getAuthProvider(),
                result.getEmail()
        ));
        return ResponseAPI.<SignInResponse>builder()
                .code(HttpStatus.OK.value())
                .message("success")
                .data(result)
                .build();
    }

    @PostMapping("/refresh-token")
    ResponseAPI<RefreshTokenResponse> refreshToken(@CookieValue(name = "refreshToken") String refreshToken) throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(refreshToken);
        return ResponseAPI.<RefreshTokenResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Refreshed token success")
                .data(result)
                .build();
    }

    @PostMapping("/logout")
    ResponseAPI<Void> logout(@RequestBody @Valid LogoutRequest request, HttpServletResponse response) {
        authenticationService.signOut(request, response);
        SignOnUtils.clear();
        return ResponseAPI.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Sign out success")
                .build();
    }
}

