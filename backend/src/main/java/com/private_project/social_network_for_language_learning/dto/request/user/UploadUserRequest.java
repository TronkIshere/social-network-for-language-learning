package com.private_project.social_network_for_language_learning.dto.request.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadUserRequest implements Serializable {
    String name;
    String email;
    String password;
    String phoneNumber;
    String otp;
}
