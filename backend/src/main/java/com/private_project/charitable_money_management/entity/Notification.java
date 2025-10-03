package com.private_project.charitable_money_management.entity;

import com.private_project.charitable_money_management.entity.common.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "notification")
public class Notification extends AbstractEntity<UUID> {
    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "is_read")
    private Boolean isRead = false;
}
