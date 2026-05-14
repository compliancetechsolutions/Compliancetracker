package com.compliance.common.constants;

public final class AppConstants {

	private AppConstants() {
	}

	// JWT claim keys
	public static final String CLAIM_ROLES = "roles";
	public static final String CLAIM_USER_ID = "userId";

	// ── Gateway → Service header names ───────────────────────────────────────
	public static final String HEADER_USER_ID = "X-User-Id";
	public static final String HEADER_USERNAME = "X-Username";
	public static final String HEADER_ROLES = "X-Roles";
	public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";

	// ── Pagination defaults ───────────────────────────────────────────────────
	public static final int DEFAULT_PAGE_SIZE = 20;
	public static final int MAX_PAGE_SIZE = 100;

	// ── Status values ─────────────────────────────────────────────────────────
	public static final String STATUS_ACTIVE = "ACTIVE";
	public static final String STATUS_INACTIVE = "INACTIVE";
	public static final String STATUS_SUSPENDED = "SUSPENDED";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILED = "FAILED";

}
