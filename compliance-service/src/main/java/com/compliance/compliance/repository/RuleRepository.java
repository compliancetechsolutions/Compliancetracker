package com.compliance.compliance.repository;

import java.util.List;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.compliance.common.repository.BaseRepository;

import com.compliance.compliance.entity.ComplianceRule;

@Repository

public interface RuleRepository extends BaseRepository<ComplianceRule, UUID> {

	List<ComplianceRule> findByActiveTrue();
	List<ComplianceRule> findByEntityType(String entityType);
	List<ComplianceRule> findByCountryCode(String countryCode);

}
