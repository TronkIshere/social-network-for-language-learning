package com.private_project.social_network_for_language_learning.configuration.jwt;

import com.private_project.social_network_for_language_learning.configuration.UserPrincipal;
import com.private_project.social_network_for_language_learning.dto.response.authentication.SignInStatus;
import com.private_project.social_network_for_language_learning.service.JwtService;
import com.private_project.social_network_for_language_learning.service.impl.UserDetailsServiceCustomizer;
import com.private_project.social_network_for_language_learning.util.SignOnUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsServiceCustomizer customUserDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String jwt = jwtService.getJwtFromRequest(request);
        try {
            if (!isValidToken(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = getUserDetailsByJwt(jwt);

            if (userDetails instanceof UserPrincipal user) {
                SignOnUtils.set(new SignOnUtils.SignOnUser(
                        user.getId(),jwt,null,SignInStatus.valueOf("SUCCESS"),null,user.getEmail()
                ));
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            logAuthenticationStatus();

            filterChain.doFilter(request, response);
        } finally {
            SignOnUtils.clear();
        }
    }

    private UserDetails getUserDetailsByJwt(String jwt) {
        String email = jwtService.getEmailFromToken(jwt);
        return customUserDetailsService.loadUserByUsername(email);
    }

    private boolean isValidToken(String jwt) {
        return StringUtils.hasText(jwt) && jwtService.validateToken(jwt);
    }

    private void logAuthenticationStatus() {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth != null) {
            log.debug("[CONTEXT] Current auth: {} | Roles: {}",
                    currentAuth.getName(),
                    currentAuth.getAuthorities());
        } else {
            log.warn("[CONTEXT] SecurityContext is empty after authentication attempt");
        }
    }
}
