package com.compliance.notification.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.compliance.enums.NotificationChannel;
import com.compliance.notification.entity.NotificationRecord;
import com.compliance.notification.integration.sender.NotificationSender;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSenderDispatcher {

  private final List<NotificationSender> senders;

  private Map<NotificationChannel, NotificationSender> senderMap;

// =====================================================
// INIT
// =====================================================

  @PostConstruct
  public void init() {

    senderMap =
        senders.stream()

            .collect(

                Collectors.toMap(

                    NotificationSender::supports,

                    Function.identity(),

                    (existing, replacement) -> existing

                )

            );

    log.info(

        "Loaded notification senders {}",

        senderMap.keySet()

    );

  }

// =====================================================
// SEND
// =====================================================

  public void send(

      NotificationRecord record

  ) {

    if (

    record == null

    ) {

      throw new IllegalArgumentException(

          "Notification cannot be null"

      );

    }

    NotificationChannel channel =

        record.getChannel();

    NotificationSender sender =

        senderMap.get(

            channel

        );

    if (

    sender == null

    ) {

      throw new IllegalStateException(

          "No sender configured for channel: %s"

              .formatted(

                  channel

              )

      );

    }

    log.info(

        "Dispatching notification={} channel={}",

        record.getId(),

        channel

    );

    sender.send(

        record

    );

  }

}