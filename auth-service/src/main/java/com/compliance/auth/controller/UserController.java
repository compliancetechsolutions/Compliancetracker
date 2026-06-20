package com.compliance.auth.controller;

import java.util.List;
import java.util.UUID;



import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.compliance.auth.dto.BulkCreateUserRequestDto;
import com.compliance.auth.dto.BulkUserCreateResponseDto;
import com.compliance.auth.dto.CreateUserRequestDto;
import com.compliance.auth.dto.RoleAssignmentResultDto;
import com.compliance.auth.dto.UpdateUserRequestDto;
import com.compliance.auth.dto.UserResponseDto;
import com.compliance.auth.service.UserService;
import com.compliance.common.controller.BaseController;
import com.compliance.common.dto.ApiResponse;
import com.compliance.common.dto.PaginationRequestDto;
import com.compliance.common.enums.UserErrorCode;
import com.compliance.common.exception.ResourceNotFoundException;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Users", description = "User account and role management")
@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class UserController extends BaseController {

  private final UserService userService;

  // =====================================================
  // CREATE
  // =====================================================

  @Operation(summary = "Create a new user account", description = "Admin only")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@Valid @RequestBody CreateUserRequestDto request) {

    log.info("CREATE USER HIT");

    UserResponseDto created = userService.create(request);

    return created("User created successfully", created);
  }

  @PostMapping("/bulk")
  public ResponseEntity<BulkUserCreateResponseDto> createBulkUsers(@RequestBody BulkCreateUserRequestDto request) {

    return ResponseEntity.ok(userService.createBulkUsers(request));
  }

  // =====================================================
  // READ ALL
  // =====================================================

  @Operation(summary = "List all users (paginated)", description = "Admin only")
  //@GetMapping
  @PostMapping("/users/search")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getAllUsers(

      @RequestBody PaginationRequestDto request) {
    
    
     Sort sort = Sort.unsorted();

     if (request.getSort() != null &&
                !request.getSort().isEmpty()) {

            String[] parts =
                    request.getSort().get(0).split(",");

            sort = Sort.by(

                    Sort.Direction.fromString(parts[1]),
                    parts[0]
            );
        }

        Pageable pageable = PageRequest.of(

            request.getPage(),
                request.getSize(),
                sort
        );

        return ok(userService.getAll(pageable));
  }

  // =====================================================
  // READ BY ID
  // =====================================================

  @Operation(summary = "Get user by ID")
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable UUID id) {

    UserResponseDto user = userService.getById(id)
        .orElseThrow(() -> new ResourceNotFoundException(UserErrorCode.USER_NOT_FOUND, id));

    return ok(user);
  }

  // =====================================================
  // READ BY USERNAME
  // =====================================================

  @Operation(summary = "Get user by username", description = "Admin only")
  @GetMapping("/by-username/{username}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<UserResponseDto>> getUserByUsername(@PathVariable String username) {

    return ok(userService.getUserByUsername(username));
  }

  // =====================================================
  // UPDATE
  // =====================================================

  @Operation(summary = "Update user profile")
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@PathVariable UUID id,

      @Valid @RequestBody UpdateUserRequestDto request) {

    return ok("User updated successfully",

        userService.update(id, request));
  }

  // =====================================================
  // DELETE
  // =====================================================

  @Operation(summary = "Soft-delete a user", description = "Admin only")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {

    userService.delete(id);

    return ok("User deleted successfully");
  }

  // =====================================================
  // ROLES
  // =====================================================

  @Operation(summary = "Get roles assigned to user", description = "Admin only")
  @GetMapping("/{id}/roles")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<List<String>>> getUserRoles(@PathVariable UUID id) {

    return ok(userService.getUserRoles(id));
  }

  @Operation(summary = "Assign one role", description = "Admin only")
  @PostMapping("/{id}/roles")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> assignRole(@PathVariable UUID id,

      @RequestParam String roleName) {

    userService.assignRoleToUser(id, roleName);

    return ok("Role assigned successfully");
  }

  @Operation(summary = "Assign multiple roles", description = "Admin only")
  @PostMapping("/{id}/roles/bulk")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<RoleAssignmentResultDto>> assignRoles(@PathVariable UUID id,

      @RequestBody List<String> roles) {

    RoleAssignmentResultDto result = userService.assignRolesToUser(id, roles);

    return ok("Roles processed successfully", result);
  }

  @Operation(summary = "Remove role", description = "Admin only")
  @DeleteMapping("/{id}/roles")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> removeRole(@PathVariable UUID id,

      @RequestParam String roleName) {

    userService.removeRoleFromUser(id, roleName);

    return ok("Role removed successfully");
  }
}