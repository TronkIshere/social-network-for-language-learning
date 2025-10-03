package com.private_project.charitable_money_management.configuration;

import com.private_project.charitable_money_management.entity.AuthProvider;
import com.private_project.charitable_money_management.entity.Role;
import com.private_project.charitable_money_management.entity.User;
import com.private_project.charitable_money_management.entity.UserProvider;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPrincipal implements OAuth2User, UserDetails {
    UUID id;
    String name;
    String email;
    String password;
    String principalName;
    Collection<Role> roles = new HashSet<>();
    Map<String, Object> attributes;

    public UserPrincipal(UUID id, String email, String name, String password, Collection<Role> roles, String principalName) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.roles = roles;
        this.principalName = principalName;
    }

    public static UserPrincipal create(User user) {
        String password = null;
        if (user.getUserProviders() != null) {
            for (UserProvider provider : user.getUserProviders()) {
                if (provider.getProvider() == AuthProvider.LOCAL) {
                    password = provider.getPassword();
                    break;
                }
            }
        }

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getName(),
                password,
                user.getRoles(),
                determinePrincipalName(user)
        );
    }

    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);

        if (attributes != null && attributes.containsKey("name")) {
            userPrincipal.setName((String) attributes.get("name"));
        }

        return userPrincipal;
    }

    private static String determinePrincipalName(User user) {
        if (StringUtils.hasText(user.getEmail())) {
            return user.getEmail();
        }

        if (user.getUserProviders() != null && !user.getUserProviders().isEmpty()) {
            UserProvider provider = user.getUserProviders().get(0);
            return String.format("%s_%s",
                    provider.getProvider().name().toLowerCase(),
                    provider.getProviderId());
        }

        throw new IllegalArgumentException("Cannot determine principal name - user has neither email nor any auth provider");
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                ", attributes=" + attributes +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public String getName() {
        return this.principalName != null ? this.principalName :
                this.email != null ? this.email :
                        (this.attributes != null ? (String) this.attributes.get("sub") : "unknown");
    }
}
