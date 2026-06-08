package com.compliance.auth.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.compliance.auth.service.CustomUserDetailsService;
import com.compliance.common.security.UserContextFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomUserDetailsService userDetailsService;

	// =========================
	// PASSWORD ENCODER
	// =========================
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// =========================
	// DAO AUTH PROVIDER
	// =========================
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider(CustomUserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {

		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

		provider.setPasswordEncoder(passwordEncoder);

		return provider;
	}

	// =========================
	// AUTH MANAGER
	// =========================
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

		return config.getAuthenticationManager();
	}

	// =========================
	// USER CONTEXT FILTER
	// =========================
	@Bean
	public UserContextFilter userContextFilter() {
		return new UserContextFilter();
	}

	// =========================
	// FILTER CHAIN
	// =========================
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http

				// REGISTER PROVIDER
				.authenticationProvider(daoAuthenticationProvider(userDetailsService, passwordEncoder()))

				// DISABLE CSRF
				.csrf(csrf -> csrf.disable())

				// DISABLE FORM LOGIN
				.formLogin(form -> form.disable())

				// DISABLE BASIC AUTH
				.httpBasic(basic -> basic.disable())

				// STATELESS SESSION
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// CORS
				.cors(cors -> {

					CorsConfiguration config = new CorsConfiguration();

					config.setAllowedOriginPatterns(List.of("*"));

					config.setAllowedHeaders(List.of("*"));

					config.setAllowedMethods(List.of("*"));

					config.setAllowCredentials(true);

					UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

					source.registerCorsConfiguration("/**", config);

					cors.configurationSource(source);
				})

				// EXCEPTION HANDLING
				.exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {

					res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

					res.setContentType("application/json");

					res.getWriter().write("""
							{
							  "success": false,
							  "message": "Unauthorized",
							  "status": 401
							}
							""");
				}))

				// AUTHORIZATION
				.authorizeHttpRequests(auth -> auth

						.requestMatchers("/auth/login", "/auth/refresh", "/encode", "/internal/**", "/swagger-ui/**",
								"/swagger-ui.html", "/v3/api-docs/**", "/actuator/health", "/actuator/info")
						.permitAll()

						.anyRequest().authenticated())

				// USER CONTEXT FILTER
				.addFilterBefore(userContextFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}