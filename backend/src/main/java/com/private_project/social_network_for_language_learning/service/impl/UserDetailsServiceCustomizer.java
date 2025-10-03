package com.private_project.social_network_for_language_learning.service.impl;

import com.private_project.social_network_for_language_learning.configuration.UserPrincipal;
import com.private_project.social_network_for_language_learning.entity.User;
import com.private_project.social_network_for_language_learning.repository.UserRepository;
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
