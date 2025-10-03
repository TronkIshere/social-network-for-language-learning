package com.private_project.social_network_for_language_learning.dto.request.authentication;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendOTPRequest implements Serializable {
    String email;
}
