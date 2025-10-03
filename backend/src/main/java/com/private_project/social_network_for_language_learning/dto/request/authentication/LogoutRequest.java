package com.private_project.social_network_for_language_learning.dto.request.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogoutRequest implements Serializable {
    @NotBlank(message = "Token cannot be null")
    String accessToken;
}


