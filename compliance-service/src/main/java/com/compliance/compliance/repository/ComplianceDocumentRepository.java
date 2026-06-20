
package com.compliance.compliance.repository;

import java.util.List;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.compliance.common.repository.BaseRepository;

import com.compliance.compliance.entity.ComplianceDocument;

@Repository

public interface ComplianceDocumentRepository extends BaseRepository<ComplianceDocument, UUID> {

  List<ComplianceDocument> findByComplianceId(UUID complianceId);
  List<ComplianceDocument> findByDocumentNameContainingIgnoreCase(String documentName);

}
