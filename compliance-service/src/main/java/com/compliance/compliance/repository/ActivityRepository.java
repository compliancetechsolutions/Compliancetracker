
package com.compliance.compliance.repository;

import java.util.List;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.compliance.common.repository.BaseRepository;

import com.compliance.compliance.entity.ComplianceActivity;

@Repository

public interface ActivityRepository extends BaseRepository<ComplianceActivity, UUID> {

	List<ComplianceActivity> findByFrequency(String frequency);

}
