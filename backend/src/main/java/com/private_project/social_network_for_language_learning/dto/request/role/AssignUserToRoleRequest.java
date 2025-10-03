package com.private_project.social_network_for_language_learning.dto.request.role;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignUserToRoleRequest {
    UUID userId;
    UUID roleId;
}

