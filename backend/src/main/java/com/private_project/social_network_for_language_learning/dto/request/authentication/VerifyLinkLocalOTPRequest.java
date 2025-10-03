package com.private_project.social_network_for_language_learning.dto.request.authentication;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerifyLinkLocalOTPRequest implements Serializable {
    String email;
    String password;
    String otp;
}
