package com.compliance.auth.service;

import java.util.List;
import java.util.UUID;

import com.compliance.auth.dto.CreateUserRequestDto;
import com.compliance.auth.dto.UpdateUserRequestDto;
import com.compliance.auth.dto.UserResponseDto;

public interface UserService {
	UserResponseDto createUser(CreateUserRequestDto request);
	List<UserResponseDto> getAllUsers();
	UserResponseDto getUserById(UUID userId);
	UserResponseDto getUserByUsername(String username);
	UserResponseDto updateUser(UUID userId, UpdateUserRequestDto request);
	void deleteUser(UUID userId);
	void assignRoleToUser(UUID userId, String roleName);
	void removeRoleFromUser(UUID userId, String roleName);
	void assignRolesToUser(UUID userId, List<String> roles);
	List<String> getUserRoles(UUID userId);
}
