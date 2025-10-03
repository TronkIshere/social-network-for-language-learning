package com.private_project.social_network_for_language_learning.repository;

import com.private_project.social_network_for_language_learning.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findAllByCreatedByAndIsReadIsFalseOrderByCreatedAtDesc(String userId);
}
