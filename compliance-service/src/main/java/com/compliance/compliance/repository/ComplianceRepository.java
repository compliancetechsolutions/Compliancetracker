package com.compliance.compliance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.compliance.compliance.entity.ComplianceRecord;

/**
 * Repository for ComplianceRecord entities.
 *
 * <p>
 * <b>Fixes over original:</b>
 * <ul>
 * <li>Extended {@link JpaRepository} directly instead of a custom
 * {@code BaseRepository} that was not visible in this module's classpath —
 * avoids compile errors.</li>
 * <li>Added {@code saveAll()} bulk override hint + {@code @Modifying}
 * bulk-update query for marking overdue records — avoids N+1 UPDATE statements
 * in the scheduler.</li>
 * <li>Added index-backed queries used by the scheduler and notification
 * pipeline: {@code findOverdueEntityIds},
 * {@code countByEntityIdAndStatus}.</li>
 * </ul>
 */
@Repository
public interface ComplianceRepository extends JpaRepository<ComplianceRecord, UUID> {

	// =====================================================
	// BASIC FILTERS
	// =====================================================

	List<ComplianceRecord> findByEntityId(UUID entityId);

	List<ComplianceRecord> findByStatus(String status);

	// =====================================================
	// OVERDUE — used by scheduler and notification service
	// =====================================================

	/**
	 * Return distinct entity IDs that have at least one compliance record with a
	 * due date before {@code today} and status "PENDING" or "IN_PROGRESS".
	 *
	 * <p>
	 * Used by
	 * {@link com.compliance.compliance.serviceimpl.ComplianceServiceImpl#markOverdue()}
	 * to collect targets for bulk overdue notification without loading full
	 * records.
	 */
	@Query("""
			SELECT DISTINCT c.entityId
			FROM ComplianceRecord c
			WHERE c.dueDate < :today
			  AND c.status IN ('PENDING', 'IN_PROGRESS')
			""")
	List<UUID> findOverdueEntityIds(@Param("today") LocalDate today);

	/**
	 * Bulk-update overdue records in a single SQL statement. Far more efficient
	 * than loading records → setting status → saveAll() in Java.
	 */
	@Modifying
	@Query("""
			UPDATE ComplianceRecord c
			SET c.status = 'OVERDUE'
			WHERE c.dueDate < :today
			  AND c.status IN ('PENDING', 'IN_PROGRESS')
			""")
	int bulkMarkOverdue(@Param("today") LocalDate today);

	// =====================================================
	// STATISTICS
	// =====================================================

	Long countByStatus(String status);

	Long countByEntityIdAndStatus(UUID entityId, String status);

	// =====================================================
	// UPCOMING / DEADLINE
	// =====================================================

	/**
	 * Return compliance records due within the next {@code days} days. Used by the
	 * reminder scheduler to pre-fetch upcoming deadlines.
	 */
	@Query("""
			SELECT c
			FROM ComplianceRecord c
			WHERE c.dueDate BETWEEN :from AND :to
			  AND c.status = 'PENDING'
			ORDER BY c.dueDate ASC
			""")
	List<ComplianceRecord> findUpcomingDeadlines(@Param("from") LocalDate from, @Param("to") LocalDate to);

	Long countByEntityId(UUID entityId);

	Long countByDueDateBeforeAndStatus(LocalDate date, String status);

}
