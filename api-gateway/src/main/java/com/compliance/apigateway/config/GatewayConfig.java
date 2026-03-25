/*
 * package com.compliance.apigateway.config;
 * 
 * import org.springframework.cloud.gateway.route.RouteLocator; import
 * org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder; import
 * org.springframework.context.annotation.Bean;
 * 
 * public class GatewayConfig {
 * 
 * @Bean public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
 * 
 * return builder.routes()
 * 
 * .route("auth-service", r -> r.path("/auth/**") .uri("lb://AUTH-SERVICE"))
 * 
 * .route("entity-service", r -> r.path("/entity/**")
 * .uri("lb://ENTITY-SERVICE"))
 * 
 * .route("compliance-service", r -> r.path("/compliance/**")
 * .uri("lb://COMPLIANCE-SERVICE"))
 * 
 * .route("notification-service", r -> r.path("/notification/**")
 * .uri("lb://NOTIFICATION-SERVICE"))
 * 
 * .route("audit-service", r -> r.path("/audit/**") .uri("lb://AUDIT-SERVICE"))
 * 
 * .route("archive-service", r -> r.path("/archive/**")
 * .uri("lb://ARCHIVE-SERVICE"))
 * 
 * .route("initiator-service", r -> r.path("/initiator/**")
 * .uri("lb://INITIATOR-SERVICE"))
 * 
 * .route("investor-service", r -> r.path("/investor/**")
 * .uri("lb://INVESTOR-SERVICE"))
 * 
 * .route("report-service", r -> r.path("/report/**")
 * .uri("lb://REPORT-SERVICE"))
 * 
 * .build(); } }
 */