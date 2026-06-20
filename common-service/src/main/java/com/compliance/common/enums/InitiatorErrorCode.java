package com.compliance.common.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InitiatorErrorCode implements BaseErrorCode {

  // =========================================
  // INITIATOR ERRORS
  // =========================================

  INITIATOR_NOT_FOUND("INIT_404", "Initiator not found", HttpStatus.NOT_FOUND),

  INITIATOR_ALREADY_EXISTS("INIT_409", "Initiator already exists", HttpStatus.CONFLICT),

  INITIATOR_NOT_AUTHORIZED("INIT_403", "Initiator not authorized", HttpStatus.FORBIDDEN),

  INITIATOR_APPROVAL_PENDING("INIT_410", "Initiator approval pending", HttpStatus.BAD_REQUEST),

  INITIATOR_REJECTED("INIT_411", "Initiator request rejected", HttpStatus.BAD_REQUEST),

  INITIATOR_WORKFLOW_FAILED("INIT_500", "Initiator workflow processing failed", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;

  private final String message;

  private final HttpStatus status;
}