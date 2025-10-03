package com.private_project.charitable_money_management.dto.response.authentication;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerifyOTPResponse implements Serializable {
    SignInStatus status;
    String email;
}
