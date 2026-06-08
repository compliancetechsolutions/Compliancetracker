package com.compliance.common.exception;

import com.compliance.common.enums.BaseErrorCode;

import lombok.Getter;

/**
 * Base for all domain/business exceptions.
 *
 * The BaseErrorCode carries:
 * - error code
 * - message
 * - HTTP status
 */
@Getter
public class BaseException extends RuntimeException {

    private static final long serialVersionUID =
            6517285502347750779L;

    private final BaseErrorCode errorCode;

    // =========================================
    // DEFAULT CONSTRUCTOR
    // =========================================

    public BaseException(
            BaseErrorCode errorCode
    ) {

        super(errorCode.getMessage());

        this.errorCode = errorCode;
    }

    // =========================================
    // DETAIL CONSTRUCTOR
    // =========================================

    public BaseException(
            BaseErrorCode errorCode,
            String detail
    ) {

        super(
                detail != null &&
                !detail.isBlank()
                        ? detail
                        : errorCode.getMessage()
        );

        this.errorCode = errorCode;
    }

    // =========================================
    // CAUSE CONSTRUCTOR
    // =========================================

    public BaseException(
            BaseErrorCode errorCode,
            Throwable cause
    ) {

        super(
                errorCode.getMessage(),
                cause
        );

        this.errorCode = errorCode;
    }
}