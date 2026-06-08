package com.compliance.compliance.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compliance.compliance.domain.RuleResult;
import com.compliance.compliance.dto.RuleResultDto;
import com.compliance.compliance.entity.ComplianceRecord;
import com.compliance.compliance.entity.ComplianceRule;
import com.compliance.compliance.repository.ComplianceRepository;
import com.compliance.compliance.repository.RuleRepository;
import com.compliance.compliance.service.ComplianceRuleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Compliance rule execution service using Drools.
 *
 * <p>
 * <b>Critical fixes over original code:</b>
 * <ol>
 * <li><b>KieSession singleton bug (CRITICAL)</b>: Original injected
 * {@code KieSession} as a singleton Spring bean and reused it across threads.
 * {@code KieSession} is <em>not thread-safe</em>. Under concurrent load this
 * causes:
 * <ul>
 * <li>Working memory corruption (facts from thread A visible to thread B).</li>
 * <li>{@code ConcurrentModificationException} inside Drools when two threads
 * fire rules simultaneously.</li>
 * <li>Phantom rule evaluations: leftover facts from prior calls influence
 * subsequent rule firings.</li>
 * </ul>
 * Fixed by injecting {@link KieBase} (which IS thread-safe) and creating a
 * <b>new {@code KieSession} per call</b>, disposing it in a finally block. This
 * is the Drools-recommended pattern.</li>
 * <li><b>executeAllRules returned empty list</b>: Now actually fetches all
 * active rules, inserts the compliance record, fires them, and collects
 * results.</li>
 * <li><b>reloadRules was a no-op</b>: Now properly delegates to all agenda
 * groups.</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ComplianceRuleServiceImpl implements ComplianceRuleService {

	private final RuleRepository ruleRepository;
	private final ComplianceRepository complianceRepository;

	/**
	 * Thread-safe Drools knowledge base. KieBase is created once at startup and
	 * shared across threads (it is immutable). KieSession is created per call from
	 * this base.
	 */
	private final KieBase kieBase;

	// =====================================================
	// CRUD
	// =====================================================

	@Override
	public ComplianceRule create(ComplianceRule rule) {
		rule.setActive(true);
		return ruleRepository.save(rule);
	}

	@Override
	public ComplianceRule update(UUID ruleId, ComplianceRule request) {
		ComplianceRule rule = getById(ruleId);
		rule.setRuleName(request.getRuleName());
		rule.setRuleCondition(request.getRuleCondition());
		rule.setActive(request.getActive());
		rule.setMandatory(request.getMandatory());
		return ruleRepository.save(rule);
	}

	@Override
	@Transactional(readOnly = true)
	public ComplianceRule getById(UUID ruleId) {
		return ruleRepository.findById(ruleId).orElseThrow(() -> new RuntimeException("Rule not found: " + ruleId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ComplianceRule> getAll() {
		return ruleRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ComplianceRule> getActiveRules() {
		return ruleRepository.findByActiveTrue();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ComplianceRule> getByEntityType(String entityType) {
		return ruleRepository.findByEntityType(entityType);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ComplianceRule> getByCountry(String countryCode) {
		return ruleRepository.findByCountryCode(countryCode);
	}

	@Override
	public void delete(UUID ruleId) {
		ruleRepository.deleteById(ruleId);
	}

	// =====================================================
	// SINGLE RULE EXECUTION
	// =====================================================

	/**
	 * Execute one rule against a compliance record.
	 *
	 * <p>
	 * <b>FIX:</b> Creates a fresh {@code KieSession} per call and disposes it in a
	 * finally block — original reused the singleton session (not thread-safe).
	 */
	@Override
	public RuleResultDto executeRule(UUID complianceId, UUID ruleId) {
		long start = System.currentTimeMillis();
		ComplianceRule rule = getById(ruleId);

		KieSession session = kieBase.newKieSession();
		try {
			RuleResult result = new RuleResult();
			session.insert(rule);
			session.insert(result);
			int fired = session.fireAllRules();

			return RuleResultDto.builder().complianceId(complianceId).ruleId(ruleId).success(fired > 0)
					.compliant(result.isPassed()).ruleName(result.getRuleName()).ruleResult(result.getMessage())
					.firedRuleCount(fired).executionTimeMs(System.currentTimeMillis() - start)
					.executedAt(LocalDateTime.now()).build();

		} finally {
			session.dispose(); // critical — releases working memory
		}
	}

	// =====================================================
	// ALL RULES EXECUTION
	// =====================================================

	/**
	 * Execute all active rules against the given compliance record.
	 *
	 * <p>
	 * <b>FIX:</b> Original returned {@code Collections.emptyList()}
	 * unconditionally. Now fetches the compliance record, inserts all active rules
	 * as facts, fires all rules, and collects per-rule results.
	 */
	@Override
	public List<RuleResultDto> executeAllRules(UUID complianceId) {
		long start = System.currentTimeMillis();
		log.info("[RULES] executeAllRules | complianceId={}", complianceId);

		List<ComplianceRule> activeRules = ruleRepository.findByActiveTrue();
		if (activeRules.isEmpty()) {
			log.info("[RULES] No active rules to execute for complianceId={}", complianceId);
			return Collections.emptyList();
		}

		ComplianceRecord record = complianceRepository.findById(complianceId).orElse(null);

		KieSession session = kieBase.newKieSession();
		List<RuleResultDto> results = new ArrayList<>();

		try {
			// Insert compliance context
			if (record != null)
				session.insert(record);

			// Insert each rule + a result holder
			for (ComplianceRule rule : activeRules) {
				RuleResult result = new RuleResult();
				session.insert(rule);
				session.insert(result);

				int fired = session.fireAllRules();

				results.add(RuleResultDto.builder().complianceId(complianceId).ruleId(rule.getRuleId())
						.success(fired > 0).compliant(result.isPassed())
						.ruleName(rule.getRuleName() != null ? rule.getRuleName() : result.getRuleName())
						.ruleResult(result.getMessage()).firedRuleCount(fired)
						.executionTimeMs(System.currentTimeMillis() - start).executedAt(LocalDateTime.now()).build());
			}

			log.info("[RULES] executeAllRules complete | complianceId={} rulesRun={} elapsed={}ms", complianceId,
					results.size(), System.currentTimeMillis() - start);
			return results;

		} finally {
			session.dispose();
		}
	}

	// =====================================================
	// AGENDA-BASED EXECUTION
	// =====================================================

	/**
	 * Run all agenda groups in one session.
	 *
	 * <p>
	 * <b>FIX:</b> Original created a new session per agenda group call but never
	 * disposed any of them (memory leak). Now runs all agenda groups in a single
	 * session and disposes once.
	 */
	@Override
	public void reloadRules() {
		log.info("[RULES] reloadRules — executing all agenda groups");
		KieSession session = kieBase.newKieSession();
		try {
			fireAgendaGroup(session, "monthly");
			fireAgendaGroup(session, "quarterly");
			fireAgendaGroup(session, "yearly");
			fireAgendaGroup(session, "reminder");
			fireAgendaGroup(session, "overdue");
		} finally {
			session.dispose();
		}
	}

	@Override
	public void executeMonthlyRules() {
		executeAgenda("monthly");
	}

	@Override
	public void executeQuarterlyRules() {
		executeAgenda("quarterly");
	}

	@Override
	public void executeYearlyRules() {
		executeAgenda("yearly");
	}

	@Override
	public void executeReminderRules() {
		executeAgenda("reminder");
	}

	@Override
	public void executeOverdueRules() {
		executeAgenda("overdue");
	}

	// =====================================================
	// VALIDATION / STATUS
	// =====================================================

	@Override
	public boolean validateRule(ComplianceRule rule) {
		return rule != null && Boolean.TRUE.equals(rule.getActive());
	}

	@Override
	public void activate(UUID ruleId) {
		ComplianceRule rule = getById(ruleId);
		rule.setActive(true);
		ruleRepository.save(rule);
	}

	@Override
	public void deactivate(UUID ruleId) {
		ComplianceRule rule = getById(ruleId);
		rule.setActive(false);
		ruleRepository.save(rule);
	}

	// =====================================================
	// PRIVATE HELPERS
	// =====================================================

	/** Execute a single agenda group in its own session. */
	private void executeAgenda(String agenda) {
		log.info("[RULES] Executing agenda group: {}", agenda);
		KieSession session = kieBase.newKieSession();
		try {
			fireAgendaGroup(session, agenda);
		} finally {
			session.dispose();
		}
	}

	/** Set focus on an agenda group and fire all rules. */
	private void fireAgendaGroup(KieSession session, String agenda) {
		try {
			session.getAgenda().getAgendaGroup(agenda).setFocus();
			int fired = session.fireAllRules();
			log.debug("[RULES] Agenda '{}' fired {} rules", agenda, fired);
		} catch (Exception ex) {
			log.error("[RULES] Agenda '{}' execution failed: {}", agenda, ex.getMessage(), ex);
		}
	}
}
