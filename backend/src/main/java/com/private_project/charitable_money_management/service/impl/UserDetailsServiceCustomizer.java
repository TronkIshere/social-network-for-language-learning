package com.private_project.charitable_money_management.service.impl;

import com.private_project.charitable_money_management.configuration.UserPrincipal;
import com.private_project.charitable_money_management.entity.User;
import com.private_project.charitable_money_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceCustomizer implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return UserPrincipal.create(user);
    }
}
