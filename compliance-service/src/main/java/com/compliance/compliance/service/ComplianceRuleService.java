package com.compliance.compliance.service;
import java.util.List;
import java.util.UUID;
import com.compliance.compliance.dto.RuleResultDto;
import com.compliance.compliance.entity.ComplianceRule;

public interface ComplianceRuleService {

// ==========================================
// CREATE
// ==========================================

	ComplianceRule create(ComplianceRule rule);

// ==========================================
// UPDATE
// ==========================================

	ComplianceRule update(UUID ruleId,ComplianceRule rule);

// ==========================================
// GET
// ==========================================

	ComplianceRule getById(UUID ruleId);
	List<ComplianceRule>getAll();
	List<ComplianceRule>getActiveRules();
	List<ComplianceRule>getByEntityType(String entityType);
	List<ComplianceRule>getByCountry(String countryCode);

// ==========================================
// DELETE
// ==========================================

	void delete(UUID ruleId);

// ==========================================
// RULE EXECUTION
// ==========================================

	RuleResultDto executeRule(UUID complianceId,UUID ruleId);

	List<RuleResultDto>	executeAllRules(UUID complianceId);

// ==========================================
// DROOLS
// ==========================================

	void reloadRules();
	void executeMonthlyRules();
	void executeQuarterlyRules();
	void executeYearlyRules();
	void executeReminderRules();
	void executeOverdueRules();
	boolean validateRule(ComplianceRule rule);

// ==========================================
// STATUS
// ==========================================

void activate(UUID ruleId);
void deactivate(UUID ruleId);

}
