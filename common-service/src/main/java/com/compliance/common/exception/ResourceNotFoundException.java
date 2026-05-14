package com.compliance.common.exception;

import com.compliance.enums.ErrorCode;

/**
 * Thrown when a requested resource does not exist (or has been soft-deleted).
 * Maps to HTTP 404.
 */
public class ResourceNotFoundException extends BaseException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 127595026576423431L;

	public ResourceNotFoundException(ErrorCode errorCode, Object id) {
        super(errorCode, errorCode.getMessage() + " [id=" + id + "]");
    }

    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
