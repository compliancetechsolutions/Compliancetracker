package com.compliance.auth.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false);

        return new ProviderManager(provider);
    }

    @Bean
    public UserContextFilter userContextFilter() {
        return new UserContextFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

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

            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((req, res, e) -> {
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.setContentType("application/json");
                        res.getWriter().write("""
                        {"success":false,"message":"Unauthorized","status":401}
                        """);
                    })
            )

            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers(
            	            "/auth/login",
            	            "/auth/refresh",
            	            "/encode",
            	            "/internal/**",
            	            "/swagger-ui/**",
            	            "/swagger-ui.html",
            	            "/v3/api-docs/**",
            	            "/actuator/health",
            	            "/actuator/info"
            	    ).permitAll()

            	    .anyRequest().authenticated()
            	
                    // All other requests must be authenticated.
                    // Role checks are handled by @PreAuthorize on each method.
                    
            )

            // UserContextFilter MUST run before UsernamePasswordAuthenticationFilter
            // (which sits before AuthorizationFilter in the chain).
            // This ensures the SecurityContext is populated from gateway headers
            // (X-User-Id, X-Username, X-Roles) before @PreAuthorize fires.
            .addFilterBefore(userContextFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}