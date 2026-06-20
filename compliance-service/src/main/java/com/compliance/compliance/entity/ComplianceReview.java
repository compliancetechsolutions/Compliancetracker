package com.compliance.compliance.entity;

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

@Table(name = "compliance_reviews", schema = "compliance_schema")

public class ComplianceReview

    extends BaseEntity {

// ========================================
// PRIMARY KEY
// ========================================

  @Id

  @Column(name = "review_id", nullable = false)

  private UUID reviewId;

// ========================================
// REFERENCES
// ========================================

  @Column(name = "compliance_id", nullable = false)

  private UUID complianceId;

// ========================================
// REVIEW
// ========================================

  @Column(name = "reviewed_by", nullable = false)

  private UUID reviewedBy;

  @Column(name = "review_status", length = 50)

  private String reviewStatus;

}
