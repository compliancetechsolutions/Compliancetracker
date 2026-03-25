package com.compliance.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.compliance.auth.dto.LoginResponseDto;
import com.compliance.auth.service.AuthService;
import com.compliance.common.controller.BaseController;
import com.compliance.common.dto.ApiResponse;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
//ip remove
	public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestParam String username,
			@RequestParam String password, @RequestHeader(value = "X-Forwarded-For", required = false) String ip) {

		LoginResponseDto response = authService.login(username, password, ip);

		return ResponseEntity.ok(ApiResponse.success("Login successful", response));
	}

	// 🔄 REFRESH TOKEN
	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<LoginResponseDto>> refresh(@RequestParam String refreshToken) {

		LoginResponseDto response = authService.refresh(refreshToken);

		return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
	}

	// 🚪 LOGOUT
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<String>> logout(@RequestParam String refreshToken) {

		authService.logout(refreshToken);

		return ResponseEntity.ok(ApiResponse.success("Logout successful", "OK"));
	}

}
