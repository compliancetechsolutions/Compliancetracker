package com.compliance.common.exception;

import com.compliance.enums.ErrorCode;
import lombok.Getter;

/**
 * Base for all domain/business exceptions.
 *
 * <p>The {@link ErrorCode} carries the HTTP status to use — the
 * {@link GlobalExceptionHandler} reads it so each exception maps to the
 * correct status code rather than always returning 400.
 */
@Getter
public class BaseException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6517285502347750779L;
	private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCode errorCode, String detail) {
        super(detail != null && !detail.isBlank() ? detail : errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
