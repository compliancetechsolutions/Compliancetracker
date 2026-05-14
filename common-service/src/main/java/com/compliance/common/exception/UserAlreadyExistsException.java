package com.compliance.common.exception;

import com.compliance.enums.ErrorCode;

public class UserAlreadyExistsException extends BaseException {

    private static final long serialVersionUID = 1L;

    public UserAlreadyExistsException() {
        super(ErrorCode.USER_ALREADY_EXISTS);
    }

    public UserAlreadyExistsException(String detail) {
        super(ErrorCode.USER_ALREADY_EXISTS, detail);
    }

    public UserAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserAlreadyExistsException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}