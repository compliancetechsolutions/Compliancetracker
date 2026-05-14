package com.compliance.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerGatewayConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI gatewayOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Compliance Platform API Gateway")
                                .description(
                                        "Aggregated Swagger/OpenAPI documentation for all services.\n\n" +
                                        "Use the top-right dropdown in Swagger UI to switch between:\n" +
                                        "- AUTH SERVICE\n" +
                                        "- ENTITY SERVICE\n\n" +
                                        "JWT Bearer Authentication enabled."
                                )
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("Compliance Engineering Team")
                                                .email("support@compliance.com")
                                )
                                .license(
                                        new License()
                                                .name("Internal Enterprise License")
                                )
                )

                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(SECURITY_SCHEME_NAME)
                )

                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .name("Authorization")
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .in(SecurityScheme.In.HEADER)
                                )
                );
    }
}