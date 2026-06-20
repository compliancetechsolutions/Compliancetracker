package com.compliance.notification.integration.senderimpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.compliance.enums.NotificationChannel;
import com.compliance.notification.entity.NotificationRecord;
import com.compliance.notification.integration.sender.NotificationSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSenderImpl

    implements NotificationSender {

  private final JavaMailSender mailSender;

  @Value("${app.email.from}")
  private String fromAddress;

// =====================================================
// CHANNEL
// =====================================================

  @Override
  public NotificationChannel supports() {

    return NotificationChannel.EMAIL;

  }

// =====================================================
// SEND
// =====================================================

  @Override

  @Async("emailExecutor")

  @Retryable(

      retryFor = {

          MailException.class

      },

      maxAttempts = 3,

      backoff =

      @Backoff(

          delay = 1000,

          multiplier = 2

      )

  )

  public void send(

      NotificationRecord notification

  ) {

    String to =

        notification.getRecipient();

    if (

    to == null

        ||

        to.isBlank()

    ) {

      log.warn(

          "[EMAIL] Missing recipient notificationId={}",

          notification.getId()

      );

      return;

    }

    try {

      SimpleMailMessage mail =

          new SimpleMailMessage();

      mail.setFrom(

          fromAddress

      );

      mail.setTo(

          to

      );

      mail.setSubject(

          notification.getTitle()

              != null

                  ?

                  notification.getTitle()

                  :

                  "Notification"

      );

      mail.setText(

          notification.getMessage()

              != null

                  ?

                  notification.getMessage()

                  :

                  ""

      );

      mailSender.send(

          mail

      );

      log.info(

          "[EMAIL] sent id={} recipient={}",

          notification.getId(),

          mask(

              to

          )

      );

    }

    catch (

    MailException ex

    ) {

      log.warn(

          "[EMAIL] retry id={}",

          notification.getId(),

          ex

      );

      throw ex;

    }

  }

// =====================================================
// RECOVER
// =====================================================

  @Recover
  public void recover(

      MailException ex,

      NotificationRecord notification

  ) {

    log.error(

        "[EMAIL] exhausted retries id={}",

        notification.getId(),

        ex

    );

  }

// =====================================================
// MASK
// =====================================================

  private String mask(String email) {

    int idx = email.indexOf("@");

    if (idx <= 1) {
      return "***";
    }

    return

    email.charAt(0)

        +

        "***"

        +

        email.substring(idx);

  }
}
