package com.compliance.compliance.integration.kafka.auth;

import java.util.UUID;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.ParameterizedTypeReference;

import org.springframework.http.HttpMethod;

import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;

@Slf4j

@Component

@RequiredArgsConstructor

public class UserClient {

	private final RestTemplate restTemplate;

	@Value("${services.auth.base-url}")

	private String authUrl;

// ==========================================
// GET USER
// ==========================================

	public UserResponse getUser(

			UUID userId

	) {

		String url =

				authUrl

						+

						"/api/v1/users/"

						+

						userId;

		try {

			ResponseEntity<UserResponse>

			response =

					restTemplate.exchange(

							url,

							HttpMethod.GET,

							null,

							new ParameterizedTypeReference<UserResponse>() {
							}

					);

			return response.getBody();

		}

		catch (Exception ex) {

			log.error(

					"Failed to fetch user {}",

					userId

			);

			return null;

		}

	}

// ==========================================
// VALIDATE
// ==========================================

	public boolean exists(

			UUID userId

	) {

		return getUser(userId)

				!= null;

	}

// ==========================================
// DTO
// ==========================================

	public record UserResponse(

			UUID userId,

			String username,

			String email,

			String firstName,

			String lastName,

			String status

	) {

	}

}
