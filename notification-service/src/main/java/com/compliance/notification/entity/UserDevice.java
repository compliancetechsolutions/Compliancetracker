package com.compliance.notification.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_device", schema = "notification_schema")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDevice {

// =====================================
// ID
// =====================================

  @Id
  private UUID id;

// =====================================
// USER
// =====================================

  @Column(name = "user_id", nullable = false)
  private UUID userId;

// =====================================
// DEVICE
// =====================================

  @Column(name = "device_id")
  private String deviceId;

// =====================================
// PUSH TOKEN
// =====================================

  @Column(name = "push_token", columnDefinition = "TEXT")
  private String pushToken;

// =====================================
// PLATFORM
// =====================================

  @Column(name = "platform")
  private String platform;

// =====================================
// ACTIVE
// =====================================

  @Column(name = "active")
  @Builder.Default
  private Boolean active = true;

// =====================================
// AUDIT
// =====================================

  @Column(name = "created_at")
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();

}