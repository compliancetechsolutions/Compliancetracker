package com.compliance.common.logger;

import com.compliance.common.enums.KafkaErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class KafkaLogger {

  private KafkaLogger() {
  }

  public static void error(KafkaErrorCode errorCode, Throwable ex, Object... details) {

    log.error("[{}] {} details={} error={}", errorCode.getCode(), errorCode.getMessage(), details, ex.getMessage(),
        ex);
  }

  public static void warn(KafkaErrorCode errorCode, Object... details) {

    log.warn("[{}] {} details={}", errorCode.getCode(), errorCode.getMessage(), details);
  }

  public static void info(String message, Object... details) {

    log.info("{} details={}", message, details);
  }
}