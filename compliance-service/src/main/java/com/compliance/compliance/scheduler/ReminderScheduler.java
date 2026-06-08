package com.compliance.compliance.scheduler;

/**
 * REMOVED — this class was an empty stub.
 *
 * All scheduled jobs are consolidated in {@link ComplianceScheduler}.
 * Having two scheduler classes caused confusion about which one was active.
 *
 * @deprecated Use {@link ComplianceScheduler} for all scheduled compliance jobs.
 */
@Deprecated(since = "1.0", forRemoval = true)
public final class ReminderScheduler {
    // Intentionally empty — class is being phased out.
    // Do not add @Component here.
    private ReminderScheduler() {}
}