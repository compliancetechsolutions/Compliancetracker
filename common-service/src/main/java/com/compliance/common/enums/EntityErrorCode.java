package com.compliance.common.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EntityErrorCode implements BaseErrorCode {

	ENTITY_NOT_FOUND("ENTITY_404", "Entity not found", HttpStatus.NOT_FOUND),
	ENTITY_INVALID("ENTITY_400", "Invalid entity data", HttpStatus.BAD_REQUEST),
	ENTITY_ALREADY_EXISTS("ENTITY_409", "Entity already exists", HttpStatus.CONFLICT),
	ENTITY_CONFLICT("ENTITY_412_OPT", "Concurrent modification conflict — retry", HttpStatus.CONFLICT),
	ENTITY_ALREADT_MAPPED("ENTITY_410_OPT", "Concurrent modification conflict — retry", HttpStatus.CONFLICT),
	USER_ALREADY_INVESTED("ENTITY_411_OPT", "User has already invested in this entity", HttpStatus.CONFLICT),
	ENTITY_RELATIONSHIP_TYPE("ENTITY_412_OPT", "Admin relationship type not allowed to be mapped", HttpStatus.CONFLICT),
	 // ── Validation ─────────────────────────────────────────────────────────
    VALIDATION_ERROR("VALIDATION_400", "Validation failed", HttpStatus.BAD_REQUEST),

    // ── System ─────────────────────────────────────────────────────────────
    INTERNAL_SERVER_ERROR("SYS_500", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

	private final String code;
	private final String message;
	private final HttpStatus status;
}