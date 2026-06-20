package com.compliance.common.exception;

import com.compliance.common.enums.BaseErrorCode;
import com.compliance.common.enums.UserErrorCode;

public class UserAlreadyExistsException extends BaseException {

  private static final long serialVersionUID = 1L;

  public UserAlreadyExistsException() {
    super(UserErrorCode.USER_ALREADY_EXISTS);
  }

  public UserAlreadyExistsException(String detail) {
    super(UserErrorCode.USER_ALREADY_EXISTS, detail);
  }

  public UserAlreadyExistsException(BaseErrorCode errorCode) {
    super(errorCode);
  }

  public UserAlreadyExistsException(BaseErrorCode errorCode, String detail) {
    super(errorCode, detail);
  }
}