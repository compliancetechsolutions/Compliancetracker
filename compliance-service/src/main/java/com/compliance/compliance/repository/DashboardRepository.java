package com.compliance.compliance.repository;

import java.util.List;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import com.compliance.common.repository.BaseRepository;

import com.compliance.compliance.entity.ComplianceRecord;

@Repository

public interface DashboardRepository extends BaseRepository<ComplianceRecord, UUID> {

	Long countByStatus(String status);

	@Query("""

			SELECT c
			FROM ComplianceRecord c
			ORDER BY c.dueDate ASC
			""")

	List<ComplianceRecord> findUpcoming();

}
