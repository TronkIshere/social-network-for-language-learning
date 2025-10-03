package com.private_project.charitable_money_management.controller;

import com.private_project.charitable_money_management.dto.request.authentication.SendOTPRequest;
import com.private_project.charitable_money_management.dto.request.authentication.VerifyLinkLocalOTPRequest;
import com.private_project.charitable_money_management.dto.request.user.SearchUsersRequest;
import com.private_project.charitable_money_management.dto.request.user.UpdateUserRequest;
import com.private_project.charitable_money_management.dto.request.user.UploadUserRequest;
import com.private_project.charitable_money_management.dto.response.authentication.VerifyOTPResponse;
import com.private_project.charitable_money_management.dto.response.common.PageResponse;
import com.private_project.charitable_money_management.dto.response.common.ResponseAPI;
import com.private_project.charitable_money_management.dto.response.user.UserResponse;
import com.private_project.charitable_money_management.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@FieldDefaults(level = AccessLevel.PUBLIC)
public class UserController {
    private final UserService userService;

    @GetMapping("/getCurrentUser")
    ResponseAPI<UserResponse> getCurrentUser(HttpServletRequest httpServletRequest) {
        var result = userService.getCurrentUserFromRequest(httpServletRequest);
        return ResponseAPI.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("success")
                .data(result)
                .build();
    }


    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ResponseAPI<List<UserResponse>> getAllUsers() {
        var result = userService.getUsers();
        return ResponseAPI.<List<UserResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("success")
                .data(result)
                .build();
    }

    @PostMapping("/register-user")
    ResponseAPI<VerifyOTPResponse> sendRegisterOTPRequest(
            @Valid @RequestBody SendOTPRequest request) {
        var result = userService.sendRegisterOTPRequest(request);
        return ResponseAPI.<VerifyOTPResponse>builder()
                .code(HttpStatus.OK.value())
                .message("success register user")
                .data(result)
                .build();
    }

    @PostMapping("/verify-register-otp")
    ResponseAPI<UserResponse> verifyRegisterOTPRequest(
            @Valid @RequestBody UploadUserRequest request) {
        var result = userService.registerUser(request);
        return ResponseAPI.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("success register user")
                .data(result)
                .build();
    }

    @PostMapping("/link-local-user")
    ResponseAPI<VerifyOTPResponse> sendLinkLocalOTPRequest(
            @Valid @RequestBody SendOTPRequest request) {
        var result = userService.sendLinkLocalOTPRequest(request);
        return ResponseAPI.<VerifyOTPResponse>builder()
                .code(HttpStatus.OK.value())
                .message("success register user")
                .data(result)
                .build();
    }

    @PostMapping("/verify-link-local-otp")
    ResponseAPI<String> verifyLinkLocalOTPRequest(
            @Valid @RequestBody VerifyLinkLocalOTPRequest request) {
        var result = userService.verifyLinkLocalOTPRequest(request);
        return ResponseAPI.<String>builder()
                .code(HttpStatus.OK.value())
                .message("success register user")
                .data(result)
                .build();
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    ResponseAPI<UserResponse> getUserByEmail(
            @PathVariable String email) {
        var result = userService.getUser(email);
        return ResponseAPI.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("success")
                .data(result)
                .build();
    }

    @GetMapping("/list/non-admins")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ResponseAPI<PageResponse<UserResponse>> getAllUsersExceptAdmin(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "8") Integer pageSize) {
        var result = userService.getAllUserExceptAdminRole(pageNo, pageSize);
        return ResponseAPI.<PageResponse<UserResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("success")
                .data(result)
                .build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ResponseAPI<PageResponse<UserResponse>> searchUsers(
            @RequestBody SearchUsersRequest request) {
        var result = userService.searchUserByKeyWord(request);
        return ResponseAPI.<PageResponse<UserResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("success")
                .data(result)
                .build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ResponseAPI<UserResponse> getUserById(
            @PathVariable UUID userId) {
        var result = userService.getUserByUserId(userId);
        return ResponseAPI.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("success")
                .data(result)
                .build();
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    ResponseAPI<UserResponse> updateUser(
            @RequestBody UpdateUserRequest request) {
        var result = userService.updateUser(request);
        return ResponseAPI.<UserResponse>builder()
                .code(HttpStatus.OK.value())
                .message("success")
                .data(result)
                .build();
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER'))")
    ResponseAPI<String> deleteUser(
            @PathVariable("userId") UUID id) {
        userService.deleteUser(id);
        return ResponseAPI.<String>builder()
                .code(HttpStatus.OK.value())
                .message("User deleted successfully")
                .data("success")
                .build();
    }

    @PutMapping("/soft-delete/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ResponseAPI<String> softDelete(@PathVariable UUID userId) {
        userService.softDelete(userId);
        return ResponseAPI.<String>builder()
                .code(HttpStatus.OK.value())
                .message("success")
                .data("success")
                .build();
    }
}

