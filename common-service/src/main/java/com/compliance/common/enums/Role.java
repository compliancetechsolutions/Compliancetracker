package com.compliance.common.enums;

/**
 * Platform roles in ascending privilege order.
 *
 * <p>Role levels are used for hierarchical access checks:
 * {@code userRole.hasAccess(Role.ADMIN)} returns true only if the user is an ADMIN.
 * {@code userRole.hasAccess(Role.INVESTOR)} returns true for both INVESTOR and ADMIN.
 */
public enum Role {

    COMPANY_REPRESENTATIVE(1),
    INVESTOR(2),
    ADMIN(3);

    private final int level;

    Role(int level) {
        this.level = level;
    }

    /**
     * Returns true if this role has at least the privilege level of {@code required}.
     * Example: {@code ADMIN.hasAccess(INVESTOR)} → true.
     */
    public boolean hasAccess(Role required) {
        return this.level >= required.level;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Case-insensitive parse. Throws {@link IllegalArgumentException} on unknown value.
     */
    public static Role from(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role string must not be null or blank");
        }
        String normalized = role
                .trim()
                .toUpperCase()
                .replace("ROLE_", "");

        return Role.valueOf(normalized);
    }
    }

