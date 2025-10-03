package com.private_project.social_network_for_language_learning.entity;

import com.private_project.social_network_for_language_learning.entity.common.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends AbstractEntity<UUID> {
    String name;

    @ManyToMany(mappedBy = "roles")
    Collection<User> users = new HashSet<>();
}
