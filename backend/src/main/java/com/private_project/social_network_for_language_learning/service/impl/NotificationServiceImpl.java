package com.private_project.social_network_for_language_learning.service.impl;

import com.private_project.social_network_for_language_learning.dto.response.common.NotificationResponse;
import com.private_project.social_network_for_language_learning.entity.Notification;
import com.private_project.social_network_for_language_learning.mapper.NotificationMapper;
import com.private_project.social_network_for_language_learning.repository.NotificationRepository;
import com.private_project.social_network_for_language_learning.service.NotificationService;
import com.private_project.social_network_for_language_learning.util.SignOnUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public void createNotification(String userId, String title, String message, String fileName) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setFileName(fileName);
        notification.setIsRead(false);
        notification.setCreatedAt(null);
        notification.setCreatedBy(userId);
        notification.setUpdatedBy(userId);
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications() {
        String currentUser = SignOnUtils.get().getUserId().toString();
        List<Notification> notificationList = notificationRepository.findAllByCreatedByAndIsReadIsFalseOrderByCreatedAtDesc(currentUser);
        return NotificationMapper.notificationResponse(notificationList);
    }

    @Override
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        notification.setIsRead(true);
        notification.setUpdatedAt(null);
        notificationRepository.save(notification);
    }
}
