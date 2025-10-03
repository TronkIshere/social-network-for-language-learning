package com.private_project.social_network_for_language_learning.entity;

import com.private_project.social_network_for_language_learning.entity.common.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProvider extends AbstractEntity<UUID> {
    String providerId;
    String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    AuthProvider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;
}
