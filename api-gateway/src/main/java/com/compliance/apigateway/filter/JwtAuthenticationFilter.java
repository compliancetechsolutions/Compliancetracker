package com.compliance.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.compliance.apigateway.util.JwtUtil;

import reactor.core.publisher.Mono;
@Component

public class JwtAuthenticationFilter  implements GlobalFilter {
	  @Override
	    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

	        String path = exchange.getRequest().getURI().getPath();

	        if (path.contains("/auth/login") || path.contains("/auth/register")) {
	            return chain.filter(exchange);
	        }

	        if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
	            throw new RuntimeException("Missing Authorization Header");
	        }

	        String authHeader = exchange.getRequest().getHeaders()
	                .getFirst(HttpHeaders.AUTHORIZATION);

	        if (!authHeader.startsWith("Bearer ")) {
	            throw new RuntimeException("Invalid Authorization Header");
	        }

	        String token = authHeader.substring(7);

	        if (!JwtUtil.validateToken(token)) {
	            throw new RuntimeException("Invalid JWT Token");
	        }

	        return chain.filter(exchange);
	    }

}
