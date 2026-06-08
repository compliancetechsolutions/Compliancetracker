package com.compliance.common.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // ── Auth ───────────────────────────────────────────────────────────────
    AUTH_INVALID_CREDENTIALS("AUTH_401_INVALID", "Invalid username or password", HttpStatus.UNAUTHORIZED),
    AUTH_UNAUTHORIZED("AUTH_401", "Unauthorized", HttpStatus.UNAUTHORIZED),
    AUTH_FORBIDDEN("AUTH_403", "Access denied", HttpStatus.FORBIDDEN),
    AUTH_NO_ROLES("AUTH_NO_ROLES", "User has no roles assigned", HttpStatus.FORBIDDEN),
    AUTH_ACCOUNT_DISABLED("AUTH_DISABLED", "Account is disabled", HttpStatus.FORBIDDEN),
    AUTH_ACCOUNT_LOCKED("AUTH_LOCKED", "Account is locked", HttpStatus.FORBIDDEN),

    AUTH_TOKEN_EXPIRED("AUTH_TOKEN_EXP", "Token has expired", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_REVOKED("AUTH_TOKEN_REV", "Token has been revoked", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_REUSE("AUTH_TOKEN_REUSE", "Token reuse detected — all sessions revoked", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_INVALID("AUTH_TOKEN_INV", "Token is invalid", HttpStatus.UNAUTHORIZED),

    // ── User ───────────────────────────────────────────────────────────────
    USER_NOT_FOUND("USER_404", "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER_409", "Username or email already exists", HttpStatus.CONFLICT),
    USER_MUST_HAVE_ROLE("USER_NO_ROLE", "User must have at least one role", HttpStatus.BAD_REQUEST),

    // ── Role & Permission ──────────────────────────────────────────────────
    ROLE_NOT_FOUND("ROLE_404", "Role not found", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND("PERM_404", "Permission not found", HttpStatus.NOT_FOUND),

    ROLE_ASSIGNMENT_COMPLETED("ROLE_200", "Roles processed successfully", HttpStatus.OK),
    ROLE_ALREADY_ASSIGNED("ROLE_EXISTS", "Role already assigned", HttpStatus.OK),
    ROLE_PARTIAL_ASSIGNMENT("ROLE_PARTIAL", "Some roles assigned, some skipped or not found", HttpStatus.OK),

    // ── Entity ─────────────────────────────────────────────────────────────
    ENTITY_NOT_FOUND("ENTITY_404", "Entity not found", HttpStatus.NOT_FOUND),
    ENTITY_INVALID("ENTITY_400", "Invalid entity data", HttpStatus.BAD_REQUEST),
    ENTITY_ALREADY_EXISTS("ENTITY_409", "Entity already exists", HttpStatus.CONFLICT),
    ENTITY_CONFLICT("ENTITY_409_OPT", "Concurrent modification conflict — retry", HttpStatus.CONFLICT),
    ENTITY_ALREADT_MAPPED("ENTITY_410_OPT", "Concurrent modification conflict — retry", HttpStatus.CONFLICT),
    USER_ALREADY_INVESTED("ENTITY_411_OPT", "User Has already Invested in this entity — retry", HttpStatus.CONFLICT),
    ENTITY_RELATIONSHIP_TYPE("ENTITY_412_OPT", "Admin Relationship Typr not allowed to be mapped —retry", HttpStatus.CONFLICT),

    // ── Compliance ─────────────────────────────────────────────────────────
    COMPLIANCE_NOT_FOUND("COMP_404", "Compliance record not found", HttpStatus.NOT_FOUND),
    COMPLIANCE_ALREADY_EXISTS("COMP_409", "Compliance record already exists", HttpStatus.CONFLICT),

    // ── Validation ─────────────────────────────────────────────────────────
    VALIDATION_ERROR("VALIDATION_400", "Validation failed", HttpStatus.BAD_REQUEST),

    // ── System ─────────────────────────────────────────────────────────────
    INTERNAL_SERVER_ERROR("SYS_500", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}