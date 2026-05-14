package com.compliance.apigateway.util;


/**
 * Platform roles in ascending privilege order.
 */
public enum Role {

    COMPANY_REPRESENTATIVE(1),
    INVESTOR(2),
    ADMIN(3);

    private final int level;

    Role(int level) {
        this.level = level;
    }

    public boolean hasAccess(Role required) {
        return this.level >= required.level;
    }

    public int getLevel() {
        return level;
    }

    public static Role from(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role string must not be null or blank");
        }
        return Role.valueOf(role.trim().toUpperCase());
    }
}