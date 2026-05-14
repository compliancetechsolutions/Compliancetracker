package com.compliance.auth.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class BulkUserCreateResponseDto {
	private String message;
	private int totalRequested;
	private int createdCount;
	private int skippedCount;
	private List<UserResponseDto> createdUsers;
	private List<String> skippedUsers;

}
