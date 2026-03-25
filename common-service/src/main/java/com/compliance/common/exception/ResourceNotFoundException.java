package com.compliance.common.exception;

import org.springframework.http.HttpStatus;

import lombok.EqualsAndHashCode;
@EqualsAndHashCode(callSuper = true)
public class ResourceNotFoundException extends BaseException {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	 public ResourceNotFoundException(String resource, Long id) {
	        super(resource + " not found with id " + id,
	                "RESOURCE_NOT_FOUND",
	                HttpStatus.NOT_FOUND);
	    }

}
