package com.compliance.notification.scheduler;

import java.time.OffsetDateTime;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.compliance.common.kafka.event.NotificationEvent;
import com.compliance.enums.NotificationStatus;
import com.compliance.notification.entity.NotificationLog;
import com.compliance.notification.entity.NotificationRecord;
import com.compliance.notification.kafka.publisher.ExhaustedNotificationPublisher;
import com.compliance.notification.repository.NotificationRepository;
import com.compliance.notification.service.NotificationSenderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

  private static final int BATCH_SIZE = 500;

  private static final int MAX_RETRIES = 5;

  private static final int MAX_DEAD_RETRIES = 3;

  private final NotificationRepository notificationRepository;

  private final NotificationSenderService senderService;

  private final ExhaustedNotificationPublisher exhaustedPublisher;

// ======================================================
// RETRY FAILED
// ======================================================

  @Scheduled(

      fixedDelayString = "${app.scheduler.retry-failed-delay-ms:60000}")
  public void retryFailed() {

    int page = 0;

    while (true) {

      Page<NotificationRecord> batch =

          notificationRepository.findRetryable(

              NotificationStatus.FAILED,

              MAX_RETRIES,

              PageRequest.of(page, BATCH_SIZE)

          );

      if (batch.isEmpty()) {
        break;
      }

      for (

      NotificationRecord n

      :

      batch.getContent()

      ) {

        senderService.send(

            n);

      }

      if (!batch.hasNext()) {
        break;
      }

      page++;

    }

  }

// ======================================================
// RETRY DEAD
// ======================================================

  @Scheduled(

      fixedDelayString = "${app.scheduler.retry-dead-delay-ms:300000}")

  @Transactional
  public void retryDeadLetter() {

    int page = 0;

    while (true) {

      Page<NotificationRecord> batch =

          notificationRepository.findDeadRetryable(

              NotificationStatus.DEAD,

              MAX_DEAD_RETRIES,

              PageRequest.of(page, BATCH_SIZE)

          );

      if (batch.isEmpty()) {
        break;
      }

      for (

      NotificationRecord n

      :

      batch.getContent()

      ) {

        int retry =

            n.getRetryCount()

                *

                1;

        n.setRetryCount(

            retry);

        if (

        retry >= MAX_DEAD_RETRIES

        ) {

          n.setStatus(

              NotificationStatus.EXHAUSTED);

          notificationRepository.save(

              n);

          NotificationLog log =

              NotificationLog.builder()

                  .eventId(n.getEventId())

                  .aggregateId(n.getAggregateId())

                  .recipient(n.getRecipient())

                  .channel(n.getChannel())

                  .type(n.getType())

                  .status(NotificationStatus.EXHAUSTED)

                  .message(n.getMessage())

                  .retryCount(n.getRetryCount())

                  .errorMessage("Retry exhausted")

                  .build();

          NotificationEvent event =

              NotificationEvent.builder()

                  .entityId(n.getAggregateId())

                  .userId(n.getUserId())

                  .title(n.getTitle())

                  .message(n.getMessage())

                  .retryCount(n.getRetryCount())

                  .build();

          exhaustedPublisher.publish(

              log,

              event

          );

        }

        else {

          n.setStatus(

              NotificationStatus.FAILED);

          notificationRepository.save(

              n);

        }

      }

      if (!batch.hasNext()) {
        break;
      }

      page++;

    }

  }

// ======================================================
// PROCESS SCHEDULED
// ======================================================

  @Scheduled(

      fixedDelayString = "${app.scheduler.scheduled-check-delay-ms:30000}")

  public void processScheduledNotifications() {

    OffsetDateTime now =

        OffsetDateTime.now();

    int page = 0;

    while (true) {

      Page<NotificationRecord> batch =

          notificationRepository.findScheduledNotifications(

              now,

              NotificationStatus.PENDING,

              PageRequest.of(page, BATCH_SIZE)

          );

      if (batch.isEmpty()) {
        break;
      }

      for (

      NotificationRecord n

      :

      batch.getContent()

      ) {

        senderService.send(

            n);

      }

      if (!batch.hasNext()) {
        break;
      }

      page++;

    }

  }

// ======================================================
// CLEANUP
// ======================================================

  @Scheduled(

      cron = "${app.scheduler.cleanup-cron:0 0 2 * * *}")

  @Transactional
  public void cleanupOldNotifications() {

    notificationRepository.deleteOldNotifications(

        OffsetDateTime.now()

            .minusDays(30)

    );

  }

}
