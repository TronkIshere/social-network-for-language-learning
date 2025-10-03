package com.private_project.charitable_money_management.repository;

import com.private_project.charitable_money_management.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findAllByCreatedByAndIsReadIsFalseOrderByCreatedAtDesc(String userId);
}
