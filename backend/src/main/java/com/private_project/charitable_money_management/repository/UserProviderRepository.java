package com.private_project.charitable_money_management.repository;

import com.private_project.charitable_money_management.entity.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserProviderRepository extends JpaRepository<UserProvider, UUID> {

    @Query("SELECT u FROM UserProvider u WHERE u.user.email = :email")
    List<UserProvider> findUserProvidersByUserEmail(String email);
}


