
package com.compliance.compliance.event;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import lombok.EqualsAndHashCode;

import lombok.experimental.SuperBuilder;

import com.compliance.common.kafka.event.BaseEvent;

@Getter
@Setter

@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor

@EqualsAndHashCode(callSuper = true)

public class ComplianceCreatedEvent

    extends BaseEvent {

// =====================================
// COMPLIANCE
// =====================================

  /**
       * 
       */
      private static final long serialVersionUID = 2163159740327864197L;

  /**
       * 
       */
     

  private UUID complianceId;

  private UUID entityId;

  private UUID activityId;

  private UUID ruleId;

// =====================================
// BUSINESS
// =====================================

  private String complianceType;

  private String frequency;

  private String status;

// =====================================
// DATES
// =====================================

  private LocalDate dueDate;

  private LocalDate effectiveDate;

// =====================================
// NOTIFICATION
// =====================================

  private Boolean notificationRequired;

  private Integer priority;

// =====================================
// AUDIT
// =====================================

  private UUID createdBy;

  private String remarks;

}
