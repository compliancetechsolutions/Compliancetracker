package com.compliance.compliance.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.compliance.entity.BaseEntity;

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

@Getter
@Setter

@Builder

@NoArgsConstructor
@AllArgsConstructor

@Table(name = "compliance_deadlines", schema = "compliance_schema")

public class ComplianceDeadline

		extends BaseEntity {

// ========================================
// PRIMARY KEY
// ========================================

	@Id

	@Column(name = "deadline_id", nullable = false)

	private UUID deadlineId;

// ========================================
// REFERENCES
// ========================================

	@Column(name = "compliance_id", nullable = false)

	private UUID complianceId;

// ========================================
// DATES
// ========================================

	@Column(name = "due_date")

	private LocalDate dueDate;

	@Column(name = "reminder_date")

	private LocalDate reminderDate;

}
