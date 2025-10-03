package com.private_project.charitable_money_management.dto.response.authentication;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignInResponse implements Serializable {
    String accessToken;
    String refreshToken;
    SignInStatus status;
    String authProvider;
    UUID userId;
    String email;
}
