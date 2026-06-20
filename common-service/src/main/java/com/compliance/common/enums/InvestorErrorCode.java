package com.compliance.common.enums;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvestorErrorCode implements BaseErrorCode {

  // =========================================
  // INVESTOR ERRORS
  // =========================================

  INVESTOR_NOT_FOUND("INV_404", "Investor not found", HttpStatus.NOT_FOUND),
  INVESTOR_ALREADY_EXISTS("INV_409", "Investor already exists", HttpStatus.CONFLICT),
  INVESTOR_ALREADY_MAPPED("INV_410", "Investor already mapped to entity", HttpStatus.CONFLICT),
  INVESTOR_KYC_PENDING("INV_411", "Investor KYC verification pending", HttpStatus.BAD_REQUEST),
  INVESTOR_KYC_FAILED("INV_412", "Investor KYC verification failed", HttpStatus.BAD_REQUEST),
  INVESTOR_LIMIT_EXCEEDED("INV_413", "Investment limit exceeded", HttpStatus.BAD_REQUEST),
  INVESTOR_ACCESS_DENIED("INV_403", "Investor access denied", HttpStatus.FORBIDDEN),

   // ── Validation ─────────────────────────────────────────────────────────
    VALIDATION_ERROR("VALIDATION_400", "Validation failed", HttpStatus.BAD_REQUEST),

    // ── System ─────────────────────────────────────────────────────────────
    INTERNAL_SERVER_ERROR("SYS_500", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
  
  
  private final String code;
  private final String message;
  private final HttpStatus status;
}