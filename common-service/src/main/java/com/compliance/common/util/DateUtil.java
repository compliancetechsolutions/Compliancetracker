package com.compliance.common.util;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Date and time utility helpers.
 *
 * <p><b>Fix from original:</b> the original class had a single method
 * {@code getCurrentDate()} that returned a plain String by calling
 * {@code LocalDate.now().toString()}. This is problematic because:
 * <ul>
 *   <li>It calls {@code LocalDate.now()} with the system default clock,
 *       making it impossible to test time-sensitive logic without hacking
 *       the system clock.</li>
 *   <li>A single {@code toString()} utility is not worth a dedicated class.</li>
 *   <li>Returning a String instead of a typed {@code LocalDate} throws away
 *       type safety and forces callers to parse it back.</li>
 * </ul>
 *
 * <p>This version accepts an optional {@link Clock} on each call (defaulting
 * to the system clock) so tests can inject a fixed clock for deterministic
 * behaviour.
 *
 * <p>All methods are static — the class is a pure utility and should not be
 * instantiated or Spring-managed.
 */
public final class DateUtil {

    public static final DateTimeFormatter DATE_FORMAT     = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateUtil() {}

    // ── Current date/time ─────────────────────────────────────────────────────

    /** Returns today's date using the system clock. */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /** Returns today's date using the supplied clock (testable). */
    public static LocalDate today(Clock clock) {
        return LocalDate.now(clock);
    }

    /** Returns the current timestamp using the system clock. */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    // ── Compliance month helpers ───────────────────────────────────────────────

    /**
     * Returns the first day of the current month — used by the compliance
     * scheduler to reset due dates on the 1st of each month.
     */
    public static LocalDate firstDayOfCurrentMonth() {
        return LocalDate.now().withDayOfMonth(1);
    }

    /**
     * Returns the last day of the current month.
     */
    public static LocalDate lastDayOfCurrentMonth() {
        YearMonth ym = YearMonth.now();
        return ym.atEndOfMonth();
    }

    /**
     * Returns true if today is past the given due date, meaning an activity
     * with no completion recorded should be marked as overdue.
     */
    public static boolean isOverdue(LocalDate dueDate) {
        return LocalDate.now().isAfter(dueDate);
    }

    // ── Formatting ────────────────────────────────────────────────────────────

    public static String format(LocalDate date) {
        return date != null ? date.format(DATE_FORMAT) : null;
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMAT) : null;
    }
}
