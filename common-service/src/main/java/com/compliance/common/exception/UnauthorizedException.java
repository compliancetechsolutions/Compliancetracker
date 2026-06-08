package com.compliance.common.exception;

import com.compliance.common.enums.BaseErrorCode;
import com.compliance.common.enums.ErrorCode;

/**
 * Thrown when a request lacks valid authentication or the token is
 * revoked/expired. Maps to HTTP 401 or 403 depending on the {@link ErrorCode}.
 */
public class UnauthorizedException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1885329855953712856L;

	public UnauthorizedException(BaseErrorCode errorCode) {
		super(errorCode);
	}

	public UnauthorizedException(BaseErrorCode errorCode, String detail) {
		super(errorCode, detail);
	}
}
