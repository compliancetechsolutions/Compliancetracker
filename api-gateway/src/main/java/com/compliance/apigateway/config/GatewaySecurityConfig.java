package com.compliance.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        return http

                // disable csrf
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // disable default login popup
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                // 🔥 VERY IMPORTANT
                // let JwtAuthenticationFilter handle auth
                .authorizeExchange(ex -> ex
                        .anyExchange().permitAll()
                )

                .build();
    }
}