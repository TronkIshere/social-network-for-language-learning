package com.private_project.charitable_money_management.mapper;

import com.private_project.charitable_money_management.dto.response.role.RoleResponse;
import com.private_project.charitable_money_management.entity.Role;

import java.util.List;

public class RoleMapper {
    public RoleMapper() {
    }

    public static RoleResponse toRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public static List<RoleResponse> roleResponses(List<Role> roles) {
        return roles.stream()
                .map(RoleMapper::toRoleResponse)
                .toList();
    }
}

