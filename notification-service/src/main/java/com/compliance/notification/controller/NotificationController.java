package com.compliance.notification.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.compliance.common.controller.BaseController;

import com.compliance.common.dto.ApiResponse;

import com.compliance.enums.NotificationStatus;

import com.compliance.notification.entity.NotificationRecord;

import com.compliance.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController

    extends BaseController {

// =====================================================
// SERVICE
// =====================================================

  private final NotificationService notificationService;

// =====================================================
// GET BY ID
// =====================================================

  @GetMapping("/{id}")

  public ResponseEntity<ApiResponse<NotificationRecord>>

      getById(

          @PathVariable UUID id

  ) {

    log.debug(

        "Fetch notification id={}",

        id

    );

    return ok(

        "Notification fetched",

        notificationService.findById(id)

    );

  }

// =====================================================
// GET BY STATUS
// =====================================================

  @GetMapping

  public ResponseEntity<ApiResponse<Page<NotificationRecord>>>

      getByStatus(

          @RequestParam NotificationStatus status,

          @RequestParam(defaultValue = "0") int page,

          @RequestParam(defaultValue = "50") int size

  ) {

    Page<NotificationRecord> result =

        notificationService.findByStatus(

            status,

            PageRequest.of(

                page,

                size,

                Sort.by(Sort.Direction.DESC, "createdAt")

            )

        );

    return ok(

        "Notifications fetched",

        result

    );

  }

// =====================================================
// GET BY USER
// =====================================================

  @GetMapping("/user/{userId}")

  public ResponseEntity<ApiResponse<Page<NotificationRecord>>>

      getByUser(

          @PathVariable UUID userId,

          @RequestParam(defaultValue = "0") int page,

          @RequestParam(defaultValue = "50") int size

  ) {

    Page<NotificationRecord> result =

        notificationService.findByUserId(

            userId,

            PageRequest.of(

                page,

                size,

                Sort.by(Sort.Direction.DESC, "createdAt")

            )

        );

    return ok(

        "User notifications fetched",

        result

    );

  }

}
