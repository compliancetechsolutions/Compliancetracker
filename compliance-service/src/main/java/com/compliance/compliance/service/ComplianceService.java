
package com.compliance.compliance.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.compliance.compliance.dto.ComplianceRequest;
import com.compliance.compliance.dto.ComplianceResponse;
import com.compliance.compliance.event.ComplianceCompletedEvent;
import com.compliance.compliance.event.ComplianceCreatedEvent;

public interface ComplianceService {

// ======================================
// CRUD
// ======================================

	ComplianceResponse create(ComplianceRequest request);

	ComplianceResponse getById(UUID complianceId);

	Page<ComplianceResponse> getAll(Pageable pageable);

	ComplianceResponse update(UUID complianceId, ComplianceRequest request);

	void delete(UUID complianceId);

// ======================================
// EXECUTION
// ======================================

	void processCompliance(ComplianceCreatedEvent event);

	void completeCompliance(ComplianceCompletedEvent event);

	ComplianceResponse executeRules(UUID complianceId);

// ======================================
// SCHEDULER
// ======================================

	void executeDailyCompliance();

// FIX 1
	void sendReminder(LocalDate date);

// FIX 2
	void markOverdue();

// ======================================
// EVENTS
// ======================================

	boolean alreadyProcessed(UUID eventId);

}
