package com.private_project.charitable_money_management.mapper;

import com.private_project.charitable_money_management.dto.response.common.NotificationResponse;
import com.private_project.charitable_money_management.entity.Notification;

import java.util.List;

public class NotificationMapper {
    public NotificationMapper() {
    }

    public static NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .fileName(notification.getFileName())
                .build();

    }

    public static List<NotificationResponse> notificationResponse(List<Notification> notificationList) {
        return notificationList.stream()
                .map(NotificationMapper::toNotificationResponse)
                .toList();
    }
}

