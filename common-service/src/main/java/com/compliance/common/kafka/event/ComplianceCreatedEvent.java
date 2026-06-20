package com.compliance.common.kafka.event;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
  private static final long serialVersionUID = 4888471206860513083L;

private String complianceType;

private String status;

private LocalDate dueDate;

// =====================================
// ENTITY
// =====================================

private String entityType;

private String entityName;

// =====================================
// NOTIFICATION
// =====================================

private String email;

}