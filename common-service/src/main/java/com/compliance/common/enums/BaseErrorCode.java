package com.compliance.common.enums;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

	public String getCode();

	public String getMessage();

	public HttpStatus getStatus();

}
