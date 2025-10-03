package com.private_project.charitable_money_management.service;

import com.private_project.charitable_money_management.dto.request.authentication.SendOTPRequest;
import com.private_project.charitable_money_management.dto.request.authentication.VerifyLinkLocalOTPRequest;
import com.private_project.charitable_money_management.dto.request.user.SearchUsersRequest;
import com.private_project.charitable_money_management.dto.request.user.UpdateUserRequest;
import com.private_project.charitable_money_management.dto.request.user.UploadUserRequest;
import com.private_project.charitable_money_management.dto.response.authentication.VerifyOTPResponse;
import com.private_project.charitable_money_management.dto.response.common.PageResponse;
import com.private_project.charitable_money_management.dto.response.user.UserResponse;
import com.private_project.charitable_money_management.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {
    VerifyOTPResponse sendRegisterOTPRequest(SendOTPRequest request);

    List<UserResponse> getUsers();

    void deleteUser(UUID id);

    UserResponse getUser(String email);

    User getUserById(UUID userId);

    PageResponse<UserResponse> getAllUserExceptAdminRole(Integer pageNo, Integer pageSize);

    PageResponse<UserResponse> searchUserByKeyWord(SearchUsersRequest request);

    UserResponse getUserByUserId(UUID userId);

    UserResponse updateUser(UpdateUserRequest request);

    String softDelete(UUID userId);

    UserResponse registerUser(UploadUserRequest request);

    VerifyOTPResponse sendLinkLocalOTPRequest(SendOTPRequest request);

    String verifyLinkLocalOTPRequest(VerifyLinkLocalOTPRequest request);

    UserResponse getCurrentUserFromRequest(HttpServletRequest httpServletRequest);
}
