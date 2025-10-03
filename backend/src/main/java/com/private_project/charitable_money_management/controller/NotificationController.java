package com.private_project.charitable_money_management.controller;

import com.private_project.charitable_money_management.dto.response.common.NotificationResponse;
import com.private_project.charitable_money_management.dto.response.common.ResponseAPI;
import com.private_project.charitable_money_management.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@FieldDefaults(level = AccessLevel.PUBLIC)
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/unread")
    ResponseAPI<List<NotificationResponse>> getUnreadNotifications() {
        var result = notificationService.getUnreadNotifications();
        return ResponseAPI.<List<NotificationResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get data successfully")
                .data(result)
                .build();
    }

    @PostMapping("/read")
    ResponseAPI<String> markAsRead(@RequestParam UUID id) {
        notificationService.markAsRead(id);
        return ResponseAPI.<String>builder()
                .code(HttpStatus.OK.value())
                .message("Data saved successfully")
                .data("Data saved successfully")
                .build();
    }
}
