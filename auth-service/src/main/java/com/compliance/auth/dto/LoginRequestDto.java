package com.compliance.auth.dto;

import com.compliance.common.dto.BaseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoginRequestDto extends BaseDto {
	private String username;
	private String password;
}
