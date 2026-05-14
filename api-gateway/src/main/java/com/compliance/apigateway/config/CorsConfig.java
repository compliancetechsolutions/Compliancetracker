package com.compliance.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS configuration.
 *
 * <p>
 * In production inject {@code ALLOWED_ORIGINS} as a comma-separated list of
 * exact origins (e.g. {@code https://app.example.com}). The default wildcard is
 * acceptable only for local development.
 *
 * <p>
 * Note: {@code allowCredentials=true} is incompatible with
 * {@code allowedOrigins="*"}; when real origins are configured, credentials are
 * enabled automatically.
 */
@Configuration
public class CorsConfig {

	@Value("${cors.allowed-origins:*}")
	private List<String> allowedOrigins;

	@Value("${cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
	private List<String> allowedMethods;

	/*
	 * @Bean public CorsWebFilter corsWebFilter() {
	 * 
	 * CorsConfiguration config = new CorsConfiguration();
	 * 
	 * boolean wildcard = allowedOrigins.contains("*");
	 * 
	 * if (wildcard) { config.addAllowedOrigin("*"); } else { // exact origins —
	 * safe to allow credentials (cookies / auth headers)
	 * allowedOrigins.forEach(config::addAllowedOrigin);
	 * config.setAllowCredentials(true); }
	 * 
	 * config.setAllowedMethods(allowedMethods);
	 * 
	 * config.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION,
	 * HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT, "X-Correlation-ID",
	 * "X-Requested-With"));
	 * 
	 * config.setExposedHeaders(List.of("X-Correlation-ID",
	 * HttpHeaders.CONTENT_DISPOSITION));
	 * 
	 * config.setMaxAge(3600L); // preflight cache 1 hour
	 * 
	 * UrlBasedCorsConfigurationSource source = new
	 * UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/**",
	 * config);
	 * 
	 * return new CorsWebFilter(source); }
	 }*/


	@Bean
	public CorsWebFilter corsWebFilter() {

	    CorsConfiguration config = new CorsConfiguration();

	    // 🔥 VERY IMPORTANT: use patterns, not "*"
	    config.addAllowedOriginPattern("*");

	    config.addAllowedMethod("*");
	    config.addAllowedHeader("*");

	    // 🔥 MUST be TRUE for Authorization header
	    config.setAllowCredentials(true);

	    config.setExposedHeaders(List.of(
	            HttpHeaders.AUTHORIZATION,
	            "X-Correlation-ID"
	    ));

	    UrlBasedCorsConfigurationSource source =
	            new UrlBasedCorsConfigurationSource();

	    source.registerCorsConfiguration("/**", config);

	    return new CorsWebFilter(source);
	}
}