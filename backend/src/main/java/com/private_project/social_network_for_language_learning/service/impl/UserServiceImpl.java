package com.private_project.social_network_for_language_learning.service.impl;

import com.private_project.social_network_for_language_learning.dto.request.authentication.SendOTPRequest;
import com.private_project.social_network_for_language_learning.dto.request.authentication.VerifyLinkLocalOTPRequest;
import com.private_project.social_network_for_language_learning.dto.request.user.SearchUsersRequest;
import com.private_project.social_network_for_language_learning.dto.request.user.UpdateUserRequest;
import com.private_project.social_network_for_language_learning.dto.request.user.UploadUserRequest;
import com.private_project.social_network_for_language_learning.dto.response.authentication.SignInStatus;
import com.private_project.social_network_for_language_learning.dto.response.authentication.VerifyOTPResponse;
import com.private_project.social_network_for_language_learning.dto.response.common.PageResponse;
import com.private_project.social_network_for_language_learning.dto.response.user.UserResponse;
import com.private_project.social_network_for_language_learning.entity.AuthProvider;
import com.private_project.social_network_for_language_learning.entity.Role;
import com.private_project.social_network_for_language_learning.entity.User;
import com.private_project.social_network_for_language_learning.entity.UserProvider;
import com.private_project.social_network_for_language_learning.exception.ApplicationException;
import com.private_project.social_network_for_language_learning.exception.ErrorCode;
import com.private_project.social_network_for_language_learning.mapper.UserMapper;
import com.private_project.social_network_for_language_learning.repository.RoleRepository;
import com.private_project.social_network_for_language_learning.repository.UserProviderRepository;
import com.private_project.social_network_for_language_learning.repository.UserRepository;
import com.private_project.social_network_for_language_learning.service.OTPService;
import com.private_project.social_network_for_language_learning.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserProviderRepository userProviderRepository;
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    RoleRepository roleRepository;
    JwtServiceImpl jwtService;
    OTPService otpService;

    @Override
    public VerifyOTPResponse sendRegisterOTPRequest(SendOTPRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApplicationException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        otpService.sendOTP(request.getEmail());

        return VerifyOTPResponse.builder()
                .status(SignInStatus.OTP_REQUIRED)
                .email(request.getEmail())
                .build();
    }

    @Override
    public UserResponse registerUser(UploadUserRequest request) {
        if (!otpService.verifyOTP(request.getEmail(), request.getOtp())) {
            throw new ApplicationException(ErrorCode.INVALID_OTP);
        }

        User user = new User();
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());

        UserProvider userProvider = new UserProvider();
        userProvider.setProvider(AuthProvider.LOCAL);
        userProvider.setPassword(passwordEncoder.encode(request.getPassword()));
        userProvider.setUser(user);
        user.setUserProviders(List.of(userProvider));

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Collections.singletonList(role));

        userRepository.save(user);
        return UserMapper.toUserResponse(user);
    }

    @Override
    public VerifyOTPResponse sendLinkLocalOTPRequest(SendOTPRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new ApplicationException(ErrorCode.EMAIL_NOT_FOUND);
        }

        List<UserProvider> userProviders = userProviderRepository.findUserProvidersByUserEmail(request.getEmail());
        boolean hasValidProvider = userProviders.stream()
                .anyMatch(userProvider -> userProvider.getProvider() == AuthProvider.LOCAL);

        if (hasValidProvider) {
            throw new ApplicationException(ErrorCode.USER_ALREADY_HAS_LOCAL_ACCOUNT);
        }

        otpService.sendOTP(request.getEmail());

        return VerifyOTPResponse.builder()
                .status(SignInStatus.OTP_REQUIRED)
                .email(request.getEmail())
                .build();
    }

    @Override
    public String verifyLinkLocalOTPRequest(VerifyLinkLocalOTPRequest request) {
        if (!otpService.verifyOTP(request.getEmail(), request.getOtp())) {
            throw new ApplicationException(ErrorCode.INVALID_OTP);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_EXISTED));

        UserProvider userProvider = new UserProvider();
        userProvider.setProviderId(userProvider.getProviderId());
        userProvider.setProvider(AuthProvider.LOCAL);
        userProvider.setPassword(passwordEncoder.encode(request.getPassword()));
        userProvider.setUser(user);
        user.getUserProviders().add(userProvider);
        userRepository.save(user);
        return user.getEmail();
    }

    @Override
    public List<UserResponse> getUsers() {
        return UserMapper.userResponses(userRepository.findAll());
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_EXISTED));
        if (user != null) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public UserResponse getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserMapper.toUserResponse(user);
    }

    @Override
    public PageResponse<UserResponse> getAllUserExceptAdminRole(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> userPage = userRepository.getAllUserExceptAdminRole(pageable);

        List<User> userList = userPage.getContent();

        return PageResponse.<UserResponse>builder()
                .currentPage(pageNo)
                .pageSize(pageable.getPageSize())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .data(UserMapper.userResponses(userList))
                .build();
    }

    @Override
    public PageResponse<UserResponse> searchUserByKeyWord(SearchUsersRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNo(), request.getPageSize());
        Page<User> userPage = userRepository.searchUserByKeyWord(pageable, request.getKeyword());

        List<User> userList = userPage.getContent();

        return PageResponse.<UserResponse>builder()
                .currentPage(request.getPageNo())
                .pageSize(pageable.getPageSize())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .data(UserMapper.userResponses(userList))
                .build();
    }

    @Override
    public UserResponse getUserByUserId(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(UpdateUserRequest request) {
        User user = getUserById(UUID.fromString(request.getId()));
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());

        if(StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }

        userRepository.save(user);
        return UserMapper.toUserResponse(user);
    }

    @Override
    public User getUserById(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public String softDelete(UUID userId) {
        LocalDateTime deleteAt = LocalDateTime.now();
        User user = getUserById(userId);
        user.setDeletedAt(deleteAt);
        userRepository.save(user);
        return "User with ID " + userId + " has been delete at " + deleteAt;
    }

    @Override
    public UserResponse getCurrentUserFromRequest(HttpServletRequest httpServletRequest) {
        String jwt = jwtService.getJwtFromRequest(httpServletRequest);

        if(!jwtService.validateToken(jwt)) {
            throw new ApplicationException(ErrorCode.TOKEN_INVALID);
        }

        String userEmail = jwtService.getEmailFromToken(jwt);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_EXISTED));
        return UserMapper.toUserResponse(user);
    }
}
