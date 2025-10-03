package com.private_project.charitable_money_management.mapper;


import com.private_project.charitable_money_management.dto.response.user.UserResponse;
import com.private_project.charitable_money_management.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public UserMapper() {
    }

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toList()))
                .build();
    }

    public static List<UserResponse> userResponses(List<User> users) {
        return users.stream()
                .map(UserMapper::toUserResponse)
                .toList();
    }
}

