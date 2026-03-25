package com.compliance.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtUtil {
	private static final String SECRET = "compliance-secret-compliance-secret";

	private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

	public static boolean validateToken(String token) {

		try {

			Claims claims = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody();

			return claims.getSubject() != null;

		} catch (Exception e) {
			return false;
		}
	}
}
