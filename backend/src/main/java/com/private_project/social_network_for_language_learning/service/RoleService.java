package com.private_project.social_network_for_language_learning.service;

import com.private_project.social_network_for_language_learning.dto.request.role.AssignUserToRoleRequest;
import com.private_project.social_network_for_language_learning.dto.request.role.RemoveUserRoleRequest;
import com.private_project.social_network_for_language_learning.dto.request.role.RoleRequest;
import com.private_project.social_network_for_language_learning.dto.response.role.RoleResponse;
import com.private_project.social_network_for_language_learning.dto.response.user.UserResponse;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    List<RoleResponse> getRoles();

    RoleResponse createRole(RoleRequest request);

    void deleteRole(UUID id);

    UserResponse removeUserFromRole(RemoveUserRoleRequest request);

    UserResponse assignRoleToUser(AssignUserToRoleRequest request);

    RoleResponse removeAllUsersFromRole(UUID roleId);

}
