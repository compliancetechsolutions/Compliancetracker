package com.compliance.enums;

/**
 * Entity lifecycle states.
 *
 * <p>Used by {@link com.compliance.entityservice.entity.EntityMaster#status}.
 *
 * <p><b>Fix:</b> the original code stored status as a raw {@code String} with
 * magic values scattered across the codebase. Centralising here provides:
 * <ul>
 *   <li>Compile-time safety — typos are caught at build time.</li>
 *   <li>A single canonical definition discoverable via IDE.</li>
 *   <li>Easy exhaustive switch expressions in Java 21.</li>
 * </ul>
 */
public enum EntityStatus {

    /** Entity is active and subject to compliance tracking. */
    ACTIVE,

    /** Entity has been deactivated — no longer tracked for new compliance. */
    INACTIVE,

    /** Entity has been suspended — tracked but highlighted as non-compliant. */
    SUSPENDED;

    /**
     * Case-insensitive parse. Useful when reading status from request DTOs.
     * Throws {@link IllegalArgumentException} on unknown values.
     */
    public static EntityStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EntityStatus value must not be null or blank");
        }
        return EntityStatus.valueOf(value.trim().toUpperCase());
    }
}
