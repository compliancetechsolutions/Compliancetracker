package com.compliance.notification.integration.senderimpl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.Duration;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.retry.annotation.Backoff;

import org.springframework.retry.annotation.Retryable;

import org.springframework.stereotype.Component;

import com.compliance.enums.NotificationChannel;

import com.compliance.notification.entity.NotificationRecord;

import com.compliance.notification.integration.sender.NotificationSender;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushSenderImpl

    implements NotificationSender {

  @Value("${app.push.fcm.endpoint}")
  private String fcmEndpoint;

  @Value("${app.push.fcm.server-key}")
  private String fcmServerKey;

  private final ObjectMapper objectMapper;

  private final HttpClient httpClient =

      HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

// ======================================
// CHANNEL
// ======================================

  @Override
  public NotificationChannel supports() {

    return NotificationChannel.PUSH;

  }

// ======================================
// SEND
// ======================================

  @Override

  @Retryable(

      retryFor = { RuntimeException.class },

      maxAttempts = 3,

      backoff = @Backoff(delay = 1000, multiplier = 2)

  )

  public void send(

      NotificationRecord notification

  ) {

    String token =

        notification.getRecipient();

    if (

    token == null

        ||

        token.isBlank()

    ) {

      log.warn(

          "[Push] Missing token id={}",

          notification.getId()

      );

      return;

    }

    try {

      String payload =

          buildPayload(

              token,

              notification.getTitle(),

              notification.getMessage()

          );

      HttpRequest request =

          HttpRequest.newBuilder()

              .uri(URI.create(fcmEndpoint))

              .timeout(Duration.ofSeconds(10))

              .header("Content-Type", "application/json")

              .header("Authorization", "Bearer " + fcmServerKey)

              .POST(

                  HttpRequest.BodyPublishers.ofString(payload)

              )

              .build();

      HttpResponse<String> response =

          httpClient.send(

              request,

              HttpResponse.BodyHandlers.ofString()

          );

      int code =

          response.statusCode();

      if (

      code == 200

      ) {

        log.info(

            "[Push] sent id={}",

            notification.getId()

        );

        return;

      }

      if (

      code == 400

      ) {

        throw new IllegalArgumentException(

            "Invalid FCM token"

        );

      }

      throw new RuntimeException(

          "FCM status "

              +

              code

      );

    }

    catch (Exception ex) {

      throw new RuntimeException(

          ex

      );

    }

  }

// ======================================
// PAYLOAD
// ======================================

  private String buildPayload(

      String token,

      String title,

      String body

  )

      throws Exception {

    Map<String, Object> payload =

        Map.of(

            "message",

            Map.of(

                "token",

                token,

                "notification",

                Map.of(

                    "title",

                    title == null ? "" : title,

                    "body",

                    body == null ? "" : body

                )

            )

        );

    return

    objectMapper.writeValueAsString(payload);

  }

}
