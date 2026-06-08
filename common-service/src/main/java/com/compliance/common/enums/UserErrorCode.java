package com.compliance.common.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements BaseErrorCode {

	// ── User ───────────────────────────────────────────────────────────────
	USER_NOT_FOUND("USER_404", "User not found", HttpStatus.NOT_FOUND),
	USER_ALREADY_EXISTS("USER_409", "Username or email already exists", HttpStatus.CONFLICT),
	USER_MUST_HAVE_ROLE("USER_NO_ROLE", "User must have at least one role", HttpStatus.BAD_REQUEST),

	// ── Role & Permission ──────────────────────────────────────────────────
	ROLE_NOT_FOUND("ROLE_404", "Role not found", HttpStatus.NOT_FOUND),
	PERMISSION_NOT_FOUND("PERM_404", "Permission not found", HttpStatus.NOT_FOUND),

	ROLE_ASSIGNMENT_COMPLETED("ROLE_200", "Roles processed successfully", HttpStatus.OK),
	ROLE_ALREADY_ASSIGNED("ROLE_EXISTS", "Role already assigned", HttpStatus.OK),
	ROLE_PARTIAL_ASSIGNMENT("ROLE_PARTIAL", "Some roles assigned, some skipped or not found", HttpStatus.OK);

	private final String code;
	private final String message;
	private final HttpStatus status;

}
