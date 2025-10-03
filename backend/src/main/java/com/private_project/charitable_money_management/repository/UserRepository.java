package com.private_project.charitable_money_management.repository;

import com.private_project.charitable_money_management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE NOT EXISTS (SELECT r FROM u.roles r WHERE r.name = 'ROLE_ADMIN')")
    Page<User> getAllUserExceptAdminRole(Pageable pageable);

    @Query("SELECT u FROM User u WHERE lower(u.name) LIKE lower(concat('%', :keyword, '%')) OR " +
            "lower(u.phoneNumber) LIKE lower(concat('%', :keyword, '%')) OR " +
            "cast(u.id as string) LIKE lower(concat('%', :keyword, '%'))")
    Page<User> searchUserByKeyWord(Pageable pageable, String keyword);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userProviders WHERE u.email = :email")
    Optional<User> findByEmailWithProviders(@Param("email") String email);
}

