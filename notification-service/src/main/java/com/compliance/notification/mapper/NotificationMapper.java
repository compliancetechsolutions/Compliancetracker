package com.compliance.notification.mapper;

import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.compliance.enums.NotificationChannel;
import com.compliance.enums.NotificationStatus;
import com.compliance.enums.NotificationType;

import com.compliance.notification.dto.NotificationRequest;
import com.compliance.notification.dto.NotificationResponse;

import com.compliance.notification.entity.NotificationRecord;

@Mapper(componentModel = "spring", imports = { LocalDateTime.class, NotificationChannel.class, NotificationStatus.class,
    NotificationType.class })
public interface NotificationMapper {

// ====================================================
// REQUEST → ENTITY
// ====================================================

  @Mapping(target = "id", ignore = true)

  @Mapping(target = "eventId", source = "eventId")

  @Mapping(target = "aggregateId", source = "aggregateId")

  @Mapping(target = "userId", source = "userId")

  @Mapping(target = "recipient", source = "recipient")

  @Mapping(target = "title", source = "title")

  @Mapping(target = "message", source = "payload")

  @Mapping(target = "channel", expression = "java(NotificationChannel.EMAIL)")

  @Mapping(target = "type", expression = "java(NotificationType.COMPLIANCE)")

  @Mapping(target = "status", expression = "java(NotificationStatus.PENDING)")

  @Mapping(target = "createdAt", expression = "java(java.time.OffsetDateTime.now())")

  @Mapping(target = "updatedAt", expression = "java(java.time.OffsetDateTime.now())")
  @Mapping(target = "retryCount", constant = "0")

  @Mapping(target = "scheduledAt", ignore = true)

  @Mapping(target = "sentAt", ignore = true)

  @Mapping(target = "version", ignore = true)

  NotificationRecord toEntity(

      NotificationRequest request

  );

// ====================================================
// ENTITY → RESPONSE
// ====================================================

  @Mapping(target = "notificationId", source = "id")

  @Mapping(target = "status", source = "status")

  NotificationResponse toResponse(

      NotificationRecord entity

  );

}
