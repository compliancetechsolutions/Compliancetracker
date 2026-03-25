package com.compliance.auth.dto;

import java.util.Set;
import java.util.UUID;

import com.compliance.common.dto.BaseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper = true)
public class UserResponseDto extends BaseDto {
	private UUID userId;
    private String username;
    private String email;
    private String status;
    private Set<String> roles;
}
