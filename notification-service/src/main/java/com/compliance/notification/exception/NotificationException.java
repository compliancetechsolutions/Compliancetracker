package com.compliance.notification.exception;

import org.springframework.http.HttpStatus;

import com.compliance.enums.ErrorCode;

import lombok.Getter;

@Getter
public class NotificationException

    extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1791175385509941092L;

  private final String code;

  private final HttpStatus status;

// ======================================
// CONSTRUCTOR USING ERROR CODE
// ======================================

  public NotificationException(

      ErrorCode error

  ) {

    super(

        error.getMessage()

    );

    this.code =

        error.getCode();

    this.status =

        error.getStatus();

  }

// ======================================
// CUSTOM MESSAGE
// ======================================

  public NotificationException(

      ErrorCode error,

      String message

  ) {

    super(

        message

    );

    this.code =

        error.getCode();

    this.status =

        error.getStatus();

  }

// ======================================
// ROOT CAUSE
// ======================================

  public NotificationException(

      ErrorCode error,

      Throwable cause

  ) {

    super(

        error.getMessage(),

        cause

    );

    this.code =

        error.getCode();

    this.status =

        error.getStatus();

  }

// ======================================
// CUSTOM + CAUSE
// ======================================

  public NotificationException(

      ErrorCode error,

      String message,

      Throwable cause

  ) {

    super(

        message,

        cause

    );

    this.code =

        error.getCode();

    this.status =

        error.getStatus();

  }

}
