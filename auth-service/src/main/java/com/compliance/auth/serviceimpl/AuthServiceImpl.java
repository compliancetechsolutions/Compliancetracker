package com.compliance.auth.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.compliance.auth.dto.LoginResponseDto;
import com.compliance.auth.entity.LoginHistory;
import com.compliance.auth.entity.RefreshToken;
import com.compliance.auth.entity.User;
import com.compliance.auth.entity.UserSession;
import com.compliance.auth.repository.LoginHistoryRepository;
import com.compliance.auth.repository.RefreshTokenRepository;
import com.compliance.auth.repository.UserRepository;
import com.compliance.auth.repository.UserSessionRepository;
import com.compliance.auth.service.AuthService;
import com.compliance.auth.util.JwtUtil;
import com.compliance.common.exception.UnauthorizedException;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private RefreshTokenRepository refreshRepo;
	@Autowired
	private UserSessionRepository sessionRepo;
	@Autowired
	private LoginHistoryRepository loginRepo;
	@Autowired
	private JwtUtil jwtUtil;

	// 🔐 LOGIN
	@Override
	public LoginResponseDto login(String username, String password, String ip) {

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (Exception e) {
			loginRepo.save(LoginHistory.failed(username, ip));
			throw new UnauthorizedException("Invalid credentials");
		}
//create custome exeption for invalid user
		User user = userRepo.findByUsername(username).orElseThrow(() -> new UnauthorizedException("User not found"));

		List<String> roles = user.getUserRoles().stream().map(ur -> ur.getRole().getRoleName()).toList();

		if (roles.isEmpty()) {
			
			
			throw new UnauthorizedException("No roles assigned");
		}

		String accessToken = jwtUtil.generateAccessToken(username, roles);
		String refreshToken = jwtUtil.generateRefreshToken(username);

		// 🔐 HASH TOKEN
		String hashedToken = hash(refreshToken);

		RefreshToken token = new RefreshToken();
		token.setTokenId(UUID.randomUUID());
		token.setUserId(user.getUserId());
		token.setToken(hashedToken);
		token.setExpiryTime(LocalDateTime.now().plusDays(7));
		token.setRevoked(false);

		refreshRepo.save(token);

		UserSession session = new UserSession();
		session.setSessionId(UUID.randomUUID());
		session.setUserId(user.getUserId());
		session.setJwtToken(accessToken);
		session.setSessionStart(LocalDateTime.now());
		session.setActive(true);

		sessionRepo.save(session);

		loginRepo.save(LoginHistory.success(user.getUserId(), ip));

		// ✅ FIXED RESPONSE
		LoginResponseDto response = new LoginResponseDto();
		response.setAccessToken(accessToken);
		response.setRefreshToken(refreshToken);

		return response;
	}

	// 🔄 REFRESH
	@Override
	public LoginResponseDto refresh(String refreshToken) {

		String hashed = hash(refreshToken);

		RefreshToken token = refreshRepo.findByToken(hashed)
				.orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

		if (token.isRevoked() || token.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new UnauthorizedException("Token expired/revoked");
		}
//Change to unauthicatedexception
		User user = userRepo.findById(token.getUserId()).orElseThrow(() -> new UnauthorizedException("User not found"));
		List<String> roles = user.getUserRoles().stream().map(ur -> ur.getRole().getRoleName()).toList();
		if (token.isRevoked()) {
			handleTokenReuse(token.getUserId());
			throw new UnauthorizedException("Refresh token reuse detected");
		}

		// 3. EXPIRY CHECK
		if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
			throw new UnauthorizedException("Token expired");
		}

		// 🔁 ROTATE OLD TOKEN
		token.setRevoked(true);
		refreshRepo.save(token);

		// 🔁 GENERATE NEW REFRESH TOKEN
		String newRefresh = jwtUtil.generateRefreshToken(user.getUsername());

		RefreshToken newToken = new RefreshToken();
		newToken.setTokenId(UUID.randomUUID());
		newToken.setUserId(user.getUserId());
		newToken.setToken(hash(newRefresh));
		newToken.setExpiryTime(LocalDateTime.now().plusDays(7));
		newToken.setRevoked(false);

		refreshRepo.save(newToken);

		// 🔐 NEW ACCESS TOKEN
		String accessToken = jwtUtil.generateAccessToken(user.getUsername(), roles);

		// ✅ FIXED RESPONSE
		LoginResponseDto response = new LoginResponseDto();
		response.setAccessToken(accessToken);
		response.setRefreshToken(newRefresh);

		return response;
	}

	// 🚪 LOGOUT
	@Override
	public void logout(String refreshToken) {

		String hashed = hash(refreshToken);

		RefreshToken token = refreshRepo.findByToken(hashed)
				.orElseThrow(() -> new UnauthorizedException("Invalid token"));

		token.setRevoked(true);
		refreshRepo.save(token);

		sessionRepo.findByUserIdAndActiveTrue(token.getUserId()).forEach(session -> {
			session.setSessionEnd(LocalDateTime.now());
			session.setActive(false);
		});
	}

	private void handleTokenReuse(UUID userId) {

		// 🚨 Revoke ALL refresh tokens
		refreshRepo.findByUserId(userId).forEach(t -> {
			t.setRevoked(true);
			refreshRepo.save(t);
		});

		// 🚨 Kill all sessions
		sessionRepo.findByUserIdAndActiveTrue(userId).forEach(session -> {
			session.setActive(false);
			session.setSessionEnd(LocalDateTime.now());
			sessionRepo.save(session);
		});
	}

	// 🔐 HASH FUNCTION
	private String hash(String token) {
		return org.apache.commons.codec.digest.DigestUtils.sha256Hex(token);
	}
}
