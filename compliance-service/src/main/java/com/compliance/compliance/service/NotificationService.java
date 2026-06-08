package com.compliance.compliance.service;

import java.util.List;
import java.util.UUID;

import com.compliance.compliance.event.ComplianceCompletedEvent;
import com.compliance.compliance.event.ComplianceCreatedEvent;
import com.compliance.compliance.event.NotificationEvent;

/**
 * Contract for the high-throughput notification pipeline.
 *
 * <p>
 * <b>Scale target: 2 billion notifications with millisecond dispatch
 * latency.</b>
 *
 * <p>
 * Implementations must:
 * <ul>
 * <li>Publish to Kafka asynchronously (fire-and-forget from caller
 * perspective).</li>
 * <li>Never block the calling thread — all I/O must be non-blocking or
 * offloaded.</li>
 * <li>Be idempotent: duplicate calls with the same {@code complianceId} must
 * not produce duplicate Kafka records (use deduplication cache).</li>
 * <li>Support bulk publishing to minimize broker round-trips at scale.</li>
 * </ul>
 */
public interface NotificationService {

	// =====================================================
	// SINGLE NOTIFICATIONS
	// =====================================================

	/**
	 * Fire-and-forget: publish one {@link NotificationEvent} to Kafka. Returns
	 * immediately; completion/failure is logged asynchronously.
	 */
	void sendAsync(NotificationEvent event);

	/**
	 * Legacy text-only notification — wraps message in a generic event. Prefer
	 * {@link #sendAsync(NotificationEvent)} for all new call sites.
	 */
	void sendAsync(String message);

	/**
	 * Synchronous send — blocks until Kafka ACK or throws on failure. Use only for
	 * critical, low-volume compliance alerts.
	 */
	void send(String message);

	// =====================================================
	// COMPLIANCE LIFECYCLE NOTIFICATIONS
	// =====================================================

	/**
	 * Publish a "compliance created" notification to the entity. Called immediately
	 * after a new ComplianceRecord is persisted.
	 */
	void notifyComplianceCreated(ComplianceCreatedEvent event);

	/**
	 * Publish a "compliance completed / result available" notification. Dispatches
	 * to entity users with compliant/overdue outcome.
	 */
	void notifyComplianceCompleted(ComplianceCompletedEvent event);

	/**
	 * Publish a due-date reminder to all entities with upcoming deadlines. Called
	 * by the scheduler daily at 09:00.
	 *
	 * @param entityIds list of entity IDs whose deadlines are approaching
	 */
	void sendDueDateReminders(List<UUID> entityIds);

	/**
	 * Publish an overdue notification to all entities that missed their deadline.
	 *
	 * @param entityIds list of entity IDs whose compliances are overdue
	 */
	void sendOverdueNotifications(List<UUID> entityIds);

	// =====================================================
	// BULK / BATCH
	// =====================================================

	/**
	 * Batch-publish a list of pre-built events in a single producer transaction.
	 * Preferred for scheduler-triggered mass notifications (e.g., quarterly
	 * reminders).
	 *
	 * @param events list of events to publish; must not be null or empty
	 */
	void sendBulk(List<NotificationEvent> events);

	// =====================================================
	// ENTITY-TARGETED
	// =====================================================

	/**
	 * Send a typed notification directly to one entity.
	 *
	 * @param entityId         target entity
	 * @param notificationType SHORT_CODE (e.g. COMPLIANCE_OVERDUE)
	 * @param message          human-readable body
	 * @param priority         LOW / MEDIUM / HIGH / CRITICAL
	 */
	void notifyEntity(UUID entityId, String notificationType, String message, String priority);
}
