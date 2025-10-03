package com.private_project.charitable_money_management.service;

import com.private_project.charitable_money_management.dto.response.common.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void createNotification(String userId, String title, String message, String fileName);

    List<NotificationResponse> getUnreadNotifications();

    void markAsRead(UUID notificationId);
}

