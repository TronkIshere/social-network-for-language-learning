package com.private_project.social_network_for_language_learning.service;

import com.private_project.social_network_for_language_learning.dto.response.common.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void createNotification(String userId, String title, String message, String fileName);

    List<NotificationResponse> getUnreadNotifications();

    void markAsRead(UUID notificationId);
}

