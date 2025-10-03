package com.private_project.social_network_for_language_learning.controller;

import com.private_project.social_network_for_language_learning.dto.request.role.AssignUserToRoleRequest;
import com.private_project.social_network_for_language_learning.dto.request.role.RemoveUserRoleRequest;
import com.private_project.social_network_for_language_learning.dto.request.role.RoleRequest;
import com.private_project.social_network_for_language_learning.dto.response.common.ResponseAPI;
import com.private_project.social_network_for_language_learning.dto.response.role.RoleResponse;
import com.private_project.social_network_for_language_learning.dto.response.user.UserResponse;
import com.private_project.social_network_for_language_learning.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
@FieldDefaults(level = AccessLevel.PUBLIC)
public class RoleController {
    private final RoleService roleService;
    @GetMapping("/list")
    ResponseAPI<List<RoleResponse>> getAllRoles() {
        var result = roleService.getRoles();
        return ResponseAPI.<List<RoleResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get all roles successfully")
                .data(result)
                .build();
    }
    @PostMapping("/create")
    ResponseAPI<RoleResponse> createRole(@RequestBody RoleRequest request) {
        var result = roleService.createRole(request);
        return ResponseAPI.<RoleResponse>builder()
                .code(HttpStatus.OK.value())
                .message("New role created successfully!")
                .data(result)
                .build();
    }
    @DeleteMapping("/delete/{roleId}")
    ResponseAPI<String> deleteRole(@PathVariable("roleId") UUID roleId) {
        roleService.deleteRole(roleId);
        return ResponseAPI.<String>builder()
                .code(HttpStatus.OK.value())
                .message("success delete role")
                .data("Success delete role with ID:" + roleId)
                .build();
    }
    @PostMapping("/remove-all-users-from-role/{roleId}")
    ResponseAPI<RoleResponse> removeAllUsersFromRole(@PathVariable("roleId") UUID roleId) {
        var result = roleService.removeAllUsersFromRole(roleId);
        return ResponseAPI.<RoleResponse>builder()
                .code(HttpStatus.OK.value())
                .message("success")
                .data(result)
                .build();
    }
    @PostMapping("/remove-user-from-role")
    public ResponseAPI<UserResponse> removeUserFromRole(
            @RequestBody RemoveUserRoleRequest request) {
        var result = roleService.removeUserFromRole(request);
        return ResponseAPI.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("success")
                .data(result)
                .build();
    }
    @PostMapping("/assign-user-to-role")
    ResponseAPI<UserResponse> assignUserToRole(
            @RequestBody AssignUserToRoleRequest request) {
        var result = roleService.assignRoleToUser(request);
        return ResponseAPI.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("success")
                .data(result)
                .build();
    }
}