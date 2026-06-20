package com.compliance.common.kafka.event;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor

@SuperBuilder

@EqualsAndHashCode(callSuper = true)

public class UserEvent

    extends BaseEvent {

// =====================================================
// USER
// =====================================================

  /**
       * 
       */
  private static final long serialVersionUID = 1L;

  private UUID userId;

  private String username;

  private String email;

  private String firstName;

  private String lastName;

  private String status;

// =====================================================
// SECURITY
// =====================================================

  private Boolean passwordChanged;

// =====================================================
// ROLES
// =====================================================

  private Set<String> roles;

// =====================================================
// BULK
// =====================================================

  private Integer totalUsers;

  private Integer successCount;

  private Integer failedCount;

// =====================================================
// AUDIT
// =====================================================

  private String performedBy;

  private LocalDateTime actionTime;

// =====================================================
// EXTRA
// =====================================================

  private String remarks;

// =====================================================
// HELPERS
// =====================================================

  public boolean isSuccessful() {

    return

    successCount != null

        &&

        failedCount != null

        &&

        failedCount == 0;

  }

  public boolean isActive() {

    return

    status != null

        &&

        "ACTIVE"

            .equalsIgnoreCase(status);

  }

}
