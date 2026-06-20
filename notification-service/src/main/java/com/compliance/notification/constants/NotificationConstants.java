package com.compliance.notification.constants;

public final class NotificationConstants {

  private NotificationConstants() {
  }

  // =====================================================
  // RETRY / SCHEDULER
  // (topic names are now bound via NotificationTopicProperties /
  // app.kafka.topics.* — see application.yml)
  // =====================================================

  public static final int MAX_RETRIES = 5;
  public static final int RETRY_BATCH_SIZE = 500;
  public static final int CLEANUP_BATCH_SIZE = 5_000;
  /** How long SENT/SKIPPED logs are retained before cleanup. */
  public static final int LOG_RETENTION_DAYS = 30;
}