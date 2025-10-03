package com.private_project.social_network_for_language_learning.util;

import com.private_project.social_network_for_language_learning.dto.response.authentication.SignInStatus;
import lombok.*;

import java.util.UUID;

public class SignOnUtils {
    private static final ThreadLocal<SignOnUser> currentUser = new ThreadLocal<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignOnUser {
        private UUID userId;
        private String accessToken;
        private String refreshToken;
        private SignInStatus status;
        private String authProvider;
        private String email;
    }

    public static void set(SignOnUser user) {
        currentUser.set(user);
    }

    public static SignOnUser get() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}

