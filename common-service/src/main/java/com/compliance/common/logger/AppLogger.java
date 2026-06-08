package com.compliance.common.logger;

import com.compliance.common.enums.BaseErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class AppLogger {

    private AppLogger() {
    }

    public static void error(
            BaseErrorCode errorCode,
            Throwable ex,
            Object... details
    ) {

        log.error(
                "[{}] {} details={} error={}",
                errorCode.getCode(),
                errorCode.getMessage(),
                details,
                ex.getMessage(),
                ex
        );
    }

    public static void warn(
            BaseErrorCode errorCode,
            Object... details
    ) {

        log.warn(
                "[{}] {} details={}",
                errorCode.getCode(),
                errorCode.getMessage(),
                details
        );
    }

    public static void info(
            BaseErrorCode errorCode,
            Object... details
    ) {

        log.info(
                "[{}] {} details={}",
                errorCode.getCode(),
                errorCode.getMessage(),
                details
        );
    }
}