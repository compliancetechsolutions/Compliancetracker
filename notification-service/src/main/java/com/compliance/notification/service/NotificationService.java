package com.compliance.notification.service;



import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.compliance.enums.NotificationStatus;
import com.compliance.notification.entity.NotificationRecord;

public interface NotificationService {
    UUID create(com.compliance.notification.dto.NotificationRequest request);
    NotificationRecord findById(UUID id);
    Page<NotificationRecord> findByStatus(NotificationStatus status, Pageable pageable);
    Page<NotificationRecord> findByUserId(UUID userId, Pageable pageable);
    void markSent(UUID id);
    void markFailed(UUID id);
    void markDead(UUID id);
    void markExhausted(UUID id);
}
