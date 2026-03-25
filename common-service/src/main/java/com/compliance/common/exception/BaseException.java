package com.compliance.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public abstract class BaseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private final String errorCode;
	private final HttpStatus status;

	public BaseException(String message, String errorCode, HttpStatus status) {
		super(message);
		this.errorCode = errorCode;
		this.status = status;

	}
}