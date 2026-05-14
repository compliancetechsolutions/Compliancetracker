package com.compliance.auth.service;

import java.util.List;
import java.util.UUID;

import com.compliance.auth.dto.BulkCreateUserRequestDto;
import com.compliance.auth.dto.BulkUserCreateResponseDto;
import com.compliance.auth.dto.CreateUserRequestDto;
import com.compliance.auth.dto.RoleAssignmentResultDto;
import com.compliance.auth.dto.UpdateUserRequestDto;
import com.compliance.auth.dto.UserResponseDto;
import com.compliance.common.service.BaseService;

public interface UserService extends BaseService<
        CreateUserRequestDto,
        UpdateUserRequestDto,
        UserResponseDto,
        UUID> {

    UserResponseDto getUserByUsername(String username);

    void assignRoleToUser(UUID userId, String roleName);

    RoleAssignmentResultDto assignRolesToUser(
            UUID userId,
            List<String> roles);

    void removeRoleFromUser(UUID userId, String roleName);

    List<String> getUserRoles(UUID userId);
    BulkUserCreateResponseDto createBulkUsers(
            BulkCreateUserRequestDto request);
}