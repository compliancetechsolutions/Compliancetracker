
package com.compliance.compliance.repository;

import java.time.LocalDate;

import java.util.List;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.compliance.common.repository.BaseRepository;

import com.compliance.compliance.entity.ComplianceDeadline;

@Repository

public interface ComplianceDeadlineRepository extends BaseRepository<ComplianceDeadline, UUID> {
  List<ComplianceDeadline> findByReminderDate(LocalDate reminderDate);

  List<ComplianceDeadline> findByDueDateBefore(LocalDate date);

}
