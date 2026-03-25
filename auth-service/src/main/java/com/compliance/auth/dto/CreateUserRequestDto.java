package com.compliance.auth.dto;

import java.util.Set;

import com.compliance.common.dto.BaseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data

@EqualsAndHashCode(callSuper = true)
public class CreateUserRequestDto extends BaseDto {
	private String username;
	private String email;
	private String password;
	private Set<String> roles;

}
