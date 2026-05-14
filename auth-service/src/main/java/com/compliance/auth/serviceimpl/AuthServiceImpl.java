package com.compliance.auth.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compliance.auth.dto.LoginRequestDto;
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
import com.compliance.common.exception.BaseException;
import com.compliance.common.exception.UnauthorizedException;
import com.compliance.enums.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authManager;
	private final UserRepository userRepo;
	private final RefreshTokenRepository refreshRepo;
	private final UserSessionRepository sessionRepo;
	private final LoginHistoryRepository loginRepo;
	private final JwtUtil jwtUtil;

	// ── LOGIN ─────────────────────────────────────────────────────────────────
	@Override
	@Transactional
	public LoginResponseDto login(LoginRequestDto request, String ip) {

		String username = request.getUsername().trim().toLowerCase();

		try {
			authManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.getPassword()));

			User user = userRepo.findByUsernameIgnoreCase(username) // ✅ FIX
					.orElseThrow(() -> new UnauthorizedException(ErrorCode.AUTH_INVALID_CREDENTIALS));

			List<String> roles = extractRoles(user);

			if (roles == null || roles.isEmpty()) {
				throw new UnauthorizedException(ErrorCode.AUTH_NO_ROLES);
			}

			String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getUserId(), roles);
			String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

			refreshRepo.save(RefreshToken.builder().tokenId(UUID.randomUUID()).userId(user.getUserId())
					.token(hash(refreshToken)).expiryTime(LocalDateTime.now().plusDays(7)).revoked(false).build());

			sessionRepo.save(UserSession.builder().sessionId(UUID.randomUUID()).userId(user.getUserId())
					.jwtToken(accessToken).sessionStart(LocalDateTime.now()).active(true).build());

			loginRepo.save(LoginHistory.success(user.getUserId(), ip));

			log.info("Successful login userId={} roles={}", user.getUserId(), roles);

			return buildResponse(accessToken, refreshToken, user, roles);
			
		} catch (BadCredentialsException ex) {

		    log.warn("Failed login username={} ip={}", username, ip);

		    recordFailedLogin(username, ip);

		    throw new UnauthorizedException(
		            ErrorCode.AUTH_INVALID_CREDENTIALS
		    );

		} catch (UsernameNotFoundException ex) {

		    log.warn("User not found username={}", username);

		    throw new UnauthorizedException(
		            ErrorCode.AUTH_INVALID_CREDENTIALS
		    );

		} catch (DisabledException ex) {

		    throw new UnauthorizedException(
		            ErrorCode.AUTH_ACCOUNT_DISABLED
		    );

		} catch (LockedException ex) {

		    throw new UnauthorizedException(
		            ErrorCode.AUTH_ACCOUNT_LOCKED
		    );

		} catch (Exception ex) {

		    log.error("🔥 REAL LOGIN ERROR username={}", username, ex);

		    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
	// ── REFRESH ───────────────────────────────────────────────────────────────
	@Override
	@Transactional
	public LoginResponseDto refresh(String rawRefreshToken) {

		String hashedToken = hash(rawRefreshToken);

		RefreshToken stored = refreshRepo.findByToken(hashedToken)
				.orElseThrow(() -> new UnauthorizedException(ErrorCode.AUTH_TOKEN_REVOKED));

		// Token-reuse detection (refresh token rotation)
		if (stored.isRevoked()) {
			log.warn("Refresh token reuse detected — revoking all sessions userId={}", stored.getUserId());
			revokeAllTokensAndSessions(stored.getUserId());
			throw new UnauthorizedException(ErrorCode.AUTH_TOKEN_REUSE);
		}

		if (stored.isExpired()) {
			throw new UnauthorizedException(ErrorCode.AUTH_TOKEN_EXPIRED);
		}

		User user = userRepo.findById(stored.getUserId())
				.orElseThrow(() -> new UnauthorizedException(ErrorCode.USER_NOT_FOUND));

		List<String> roles = extractRoles(user);

		// Rotate: revoke old, issue new
		stored.setRevoked(true);
		refreshRepo.save(stored);

		String newAccess = jwtUtil.generateAccessToken(user.getUsername(), user.getUserId(), roles);
		String newRefresh = jwtUtil.generateRefreshToken(user.getUsername());

		refreshRepo.save(RefreshToken.builder().tokenId(UUID.randomUUID()).userId(user.getUserId())
				.token(hash(newRefresh)).expiryTime(LocalDateTime.now().plusDays(7)).revoked(false).build());

		log.info("Token refreshed userId={}", user.getUserId());

		return buildResponse(newAccess, newRefresh, user, roles);
	}

	// ── LOGOUT ────────────────────────────────────────────────────────────────
	@Override
	@Transactional
	public void logout(String rawRefreshToken) {

		String hashedToken = hash(rawRefreshToken);

		RefreshToken stored = refreshRepo.findByToken(hashedToken)
				.orElseThrow(() -> new UnauthorizedException(ErrorCode.AUTH_TOKEN_REVOKED));

		UUID userId = stored.getUserId();

		// ✅ revoke this refresh token
		stored.setRevoked(true);
		refreshRepo.save(stored);

		// ✅ deactivate all active sessions for this user
		List<UserSession> sessions = sessionRepo.findByUserIdAndActiveTrue(userId);

		sessions.forEach(s -> {
			s.setActive(false);
			s.setSessionEnd(LocalDateTime.now());
		});

		sessionRepo.saveAll(sessions);

		log.info("User logged out userId={}", userId);
	}
	// ── HELPERS ───────────────────────────────────────────────────────────────

	/** Separate transaction so the failure record always persists. */
	@Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
	protected void recordFailedLogin(String username, String ip) {
		UUID failedUserId = userRepo.findByUsername(username).map(User::getUserId).orElse(null);
		loginRepo.save(LoginHistory.failed(failedUserId, ip));
	}

	private void revokeAllTokensAndSessions(UUID userId) {
		List<RefreshToken> tokens = refreshRepo.findByUserIdAndRevokedFalse(userId);
		tokens.forEach(t -> t.setRevoked(true));
		refreshRepo.saveAll(tokens);

		List<UserSession> sessions = sessionRepo.findByUserIdAndActiveTrue(userId);
		sessions.forEach(s -> {
			s.setActive(false);
			s.setSessionEnd(LocalDateTime.now());
		});
		sessionRepo.saveAll(sessions);
	}

	private List<String> extractRoles(User user) {

		return user.getUserRoles().stream().map(ur -> "ROLE_" + ur.getRole().getRoleName()).toList();
	}

	private LoginResponseDto buildResponse(String accessToken, String refreshToken, User user, List<String> roles) {
		return LoginResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).userId(user.getUserId())
				.username(user.getUsername()).roles(roles).expiresIn(jwtUtil.getAccessTokenExpirySeconds()).build();
	}

	/** SHA-256 — safe for storing in DB index; never store raw refresh tokens. */
	private String hash(String token) {
		return DigestUtils.sha256Hex(token);
	}
}
