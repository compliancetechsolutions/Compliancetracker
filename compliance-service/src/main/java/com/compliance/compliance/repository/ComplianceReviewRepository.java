package com.compliance.compliance.repository;

import java.util.List;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.compliance.common.repository.BaseRepository;

import com.compliance.compliance.entity.ComplianceReview;

@Repository

public interface ComplianceReviewRepository extends BaseRepository<ComplianceReview,UUID>{

List<ComplianceReview>findByComplianceId(UUID complianceId);
List<ComplianceReview>findByReviewStatus(String reviewStatus);

}
