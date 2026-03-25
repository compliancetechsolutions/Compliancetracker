package com.compliance.auth.dto;

import java.util.Set;

import com.compliance.common.dto.BaseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateUserRequestDto extends BaseDto {

	private String email;
	private String status;
	private Set<String> roles;

}
