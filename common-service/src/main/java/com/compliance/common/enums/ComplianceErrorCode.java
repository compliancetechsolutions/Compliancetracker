package com.compliance.common.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ComplianceErrorCode implements BaseErrorCode {
  // ── Compliance ─────────────────────────────────────────────────────────
  COMPLIANCE_NOT_FOUND("COMP_404", "Compliance record not found", HttpStatus.NOT_FOUND),
  COMPLIANCE_ALREADY_EXISTS("COMP_409", "Compliance record already exists", HttpStatus.CONFLICT),
  INITIATOR_WORKFLOW_FAILED("INIT_500", "Initiator workflow processing failed", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;

}
