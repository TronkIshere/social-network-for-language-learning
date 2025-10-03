package com.private_project.charitable_money_management.repository;

import com.private_project.charitable_money_management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query("SELECT r FROM Role r WHERE r.name = :name")
    Optional<Role> findByName(String name);

    boolean existsByName(String roleName);
}

