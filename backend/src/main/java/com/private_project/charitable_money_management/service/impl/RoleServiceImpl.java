package com.private_project.charitable_money_management.service.impl;

import com.private_project.charitable_money_management.dto.request.role.AssignUserToRoleRequest;
import com.private_project.charitable_money_management.dto.request.role.RemoveUserRoleRequest;
import com.private_project.charitable_money_management.dto.request.role.RoleRequest;
import com.private_project.charitable_money_management.dto.response.role.RoleResponse;
import com.private_project.charitable_money_management.dto.response.user.UserResponse;
import com.private_project.charitable_money_management.entity.Role;
import com.private_project.charitable_money_management.entity.User;
import com.private_project.charitable_money_management.exception.ApplicationException;
import com.private_project.charitable_money_management.exception.ErrorCode;
import com.private_project.charitable_money_management.mapper.RoleMapper;
import com.private_project.charitable_money_management.mapper.UserMapper;
import com.private_project.charitable_money_management.repository.RoleRepository;
import com.private_project.charitable_money_management.repository.UserRepository;
import com.private_project.charitable_money_management.service.RoleService;
import com.private_project.charitable_money_management.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    UserService userService;
    UserRepository userRepository;

    @Override
    public List<RoleResponse> getRoles() {
        List<Role> roleList = roleRepository.findAll();
        return RoleMapper.roleResponses(roleList);
    }

    @Override
    public RoleResponse createRole(RoleRequest request) {
        String roleName = request.getName();
        Role role = new Role();
        role.setName("ROLE_" + roleName.toUpperCase());
        if (roleRepository.existsByName(roleName)) {
            throw new ApplicationException(ErrorCode.USER_ALREADY_EXIST_EXCEPTION);
        }
        roleRepository.save(role);
        return RoleMapper.toRoleResponse(role);
    }

    @Override
    public void deleteRole(UUID roleId) {
        this.removeAllUsersFromRole(roleId);
        roleRepository.deleteById(roleId);
    }

    @Override
    public UserResponse removeUserFromRole(RemoveUserRoleRequest request) {
        User user = userService.getUserById(request.getUserId());
        Role role = roleRepository.getReferenceById(request.getRoleId());

        if (user.getRoles().contains(role)) {
            throw new ApplicationException(ErrorCode.USER_ALREADY_EXIST_EXCEPTION);
        }
        assignRoleToUser(user, role);
        roleRepository.save(role);

        return UserMapper.toUserResponse(user);
    }

    @Override
    public UserResponse assignRoleToUser(AssignUserToRoleRequest request) {
        User user = userService.getUserById(request.getUserId());
        Role role = roleRepository.getReferenceById(request.getRoleId());

        if (user.getRoles().contains(role)) {
            throw new ApplicationException(ErrorCode.USER_ALREADY_HAS_THIS_ROLE);
        }

        assignRoleToUser(user, role);
        roleRepository.save(role);
        return UserMapper.toUserResponse(user);
    }

    @Override
    public RoleResponse removeAllUsersFromRole(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));

        for (User user : new ArrayList<>(role.getUsers())) {
            user.getRoles().remove(role);
            userRepository.save(user);
        }
        role.getUsers().clear();
        Role updatedRole = roleRepository.save(role);

        return RoleMapper.toRoleResponse(updatedRole);
    }

    private void assignRoleToUser(User user, Role role) {
        user.getRoles().add(role);
        role.getUsers().add(user);
    }
}
