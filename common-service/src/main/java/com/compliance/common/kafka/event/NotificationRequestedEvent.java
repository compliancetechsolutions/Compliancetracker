package com.compliance.common.kafka.event;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NotificationRequestedEvent extends BaseEvent {

private static final long serialVersionUID = 1L;

// ======================================
// IDS
// ======================================

private UUID notificationId;

private UUID entityId;

private UUID userId;

// ======================================
// DELIVERY
// ======================================

private String recipient;

private String channel;

// ======================================
// CONTENT
// ======================================

private String title;

private String message;

private String priority;

// ======================================
// SCHEDULING
// ======================================

private LocalDateTime scheduledAt;

// ======================================
// RETRY
// ======================================

@Builder.Default
private Integer retryCount = 0;

// ======================================
// TRACE
// ======================================

private String correlationId;

}
