package com.compliance.auth.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.compliance.auth.dto.CreateUserRequestDto;
import com.compliance.auth.dto.UpdateUserRequestDto;
import com.compliance.auth.dto.UserResponseDto;
import com.compliance.auth.service.UserService;
import com.compliance.common.controller.BaseController;

@RequestMapping("/auth")
@RestController
public class UserController extends BaseController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	// ================= CREATE =================
	@PostMapping
	public UserResponseDto createUser(@RequestBody CreateUserRequestDto request) {
		return userService.createUser(request);
	}

	// ================= READ =================
	@GetMapping
	public List<UserResponseDto> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	public UserResponseDto getUserById(@PathVariable UUID id) {
		return userService.getUserById(id);
	}

	@GetMapping("/username/{username}")
	public UserResponseDto getUserByUsername(@PathVariable String username) {
		return userService.getUserByUsername(username);
	}

	// ================= UPDATE =================
	@PutMapping("/{id}")
	public UserResponseDto updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequestDto request) {
		return userService.updateUser(id, request);
	}

	// ================= DELETE =================
	@DeleteMapping("/{id}")
	public String deleteUser(@PathVariable UUID id) {
		userService.deleteUser(id);
		return "User deleted successfully";
	}

	// ================= ROLE MANAGEMENT =================

	@PostMapping("/{id}/roles")
	public String assignRole(@PathVariable UUID id, @RequestParam String roleName) {
		userService.assignRoleToUser(id, roleName);
		return "Role assigned successfully";
	}

	@DeleteMapping("/{id}/roles")
	public String removeRole(@PathVariable UUID id, @RequestParam String roleName) {
		userService.removeRoleFromUser(id, roleName);
		return "Role removed successfully";
	}

	@PostMapping("/{id}/roles/bulk")
	public String assignRoles(@PathVariable UUID id, @RequestBody List<String> roles) {
		userService.assignRolesToUser(id, roles);
		return "Roles assigned successfully";
	}

	@GetMapping("/{id}/roles")
	public List<String> getUserRoles(@PathVariable UUID id) {
		return userService.getUserRoles(id);
	}

}
