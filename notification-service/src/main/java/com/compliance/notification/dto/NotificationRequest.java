package com.compliance.notification.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

@Builder

@NoArgsConstructor

@AllArgsConstructor
public class NotificationRequest {

// ======================================
// EVENT
// ======================================

private UUID eventId;

// ======================================
// AGGREGATE
// ======================================

private UUID aggregateId;

// ======================================
// USER
// ======================================

private UUID userId;

// ======================================
// RECIPIENT
// ======================================

private String recipient;

// ======================================
// TITLE
// ======================================

private String title;

// ======================================
// PAYLOAD
// ======================================

private String payload;

}