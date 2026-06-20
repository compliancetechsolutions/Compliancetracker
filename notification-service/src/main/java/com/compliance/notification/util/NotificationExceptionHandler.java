package com.compliance.notification.util;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.compliance.common.dto.ErrorResponse;
import com.compliance.notification.exception.NotificationException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class NotificationExceptionHandler {

  @ExceptionHandler(NotificationException.class)

  public ResponseEntity<ErrorResponse>

      handle(

          NotificationException ex,

          HttpServletRequest request

  ) {

    return ResponseEntity

        .status(

            ex.getStatus()

        )

        .body(

            ErrorResponse.builder()

                .success(false)

                .status(

                    ex.getStatus()

                        .value()

                )

                .message(

                    ex.getMessage()

                )

                .errorCode(

                    ex.getCode()

                )

                .path(

                    request.getRequestURI()

                )

                .traceId(UUID.randomUUID().toString()).timestamp(LocalDateTime.now())

                .build()

        );

  }

}
