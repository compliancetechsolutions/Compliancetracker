
package com.compliance.notification.serviceimpl;

import java.time.OffsetDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.compliance.enums.NotificationStatus;
import com.compliance.notification.entity.NotificationRecord;
import com.compliance.notification.integration.sender.SmsGateway;
import com.compliance.notification.integration.senderimpl.EmailSenderImpl;
import com.compliance.notification.integration.senderimpl.PushSenderImpl;
import com.compliance.notification.repository.NotificationRepository;
import com.compliance.notification.service.NotificationSenderService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSenderServiceImpl

    implements NotificationSenderService {

  private final EmailSenderImpl emailSender;

  private final PushSenderImpl pushSender;

  private final SmsGateway smsSender;

  private final NotificationRepository notificationRepository;

  @Override

  @Async("notificationSchedulerExecutor")

  @Transactional
  public void send(

      NotificationRecord notification

  ) {

    try {

      dispatch(notification);

      notification.setStatus(NotificationStatus.SENT);

      notification.setSentAt(OffsetDateTime.now());

      notificationRepository.save(notification);

    } catch (Exception ex) {

      notification.setRetryCount(

          notification.getRetryCount()

              *

              1

      );

      notification.setStatus(NotificationStatus.FAILED);

      notificationRepository.save(notification);

      throw ex;

    }

  }

  private void dispatch(

      NotificationRecord notification

  ) {

    switch (

    notification.getChannel()

    ) {

    case EMAIL ->

      emailSender.send(notification);

    case SMS ->

      smsSender.send(notification.getRecipient(), notification.getMessage());

    case PUSH ->

      pushSender.send(notification);

    default ->

      throw new IllegalArgumentException(

          "Unsupported channel "

              +

              notification.getChannel()

      );

    }

  }

}
