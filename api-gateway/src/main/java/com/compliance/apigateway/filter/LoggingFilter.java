package com.compliance.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Structured access logging filter.
 *
 * <p><b>Fix from original:</b> the original used {@code System.out.println}
 * which bypasses the logging framework entirely — no log level control,
 * no MDC correlation ID, no structured output, and no way to disable it
 * without code changes.
 *
 * <p>This version uses SLF4J at DEBUG level and logs:
 * <ul>
 *   <li>Request: method, path, remote address</li>
 *   <li>Response: status code and duration in milliseconds</li>
 * </ul>
 *
 * <p>Set {@code logging.level.com.compliance.apigateway.filter.LoggingFilter=DEBUG}
 * to enable access logging. Production default is INFO (disabled).
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        long start = System.currentTimeMillis();

        String method  = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        String path    = request.getURI().getPath();
        String remote  = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        String corrId  = request.getHeaders().getFirst("X-Correlation-ID");

        log.debug("→ {} {} remote={} correlationId={}", method, path, remote, corrId);

        return chain.filter(exchange).doFinally(signalType -> {
            long durationMs = System.currentTimeMillis() - start;
            int status = exchange.getResponse().getStatusCode() != null
                    ? exchange.getResponse().getStatusCode().value() : 0;

            log.debug("← {} {} status={} duration={}ms correlationId={}",
                    method, path, status, durationMs, corrId);
        });
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE; // run last so all other filters have executed
    }
}