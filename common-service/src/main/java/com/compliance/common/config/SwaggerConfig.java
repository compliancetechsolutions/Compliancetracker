package com.compliance.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger configuration shared across all services via common-service.
 *
 * <p><b>Fixes from original:</b>
 * <ul>
 *   <li><b>Real email address removed.</b> The original had
 *       {@code compliancetech@rediffmail.com} hard-coded in source code.
 *       Contact details and server URLs vary per environment and should never
 *       be committed. They are now injected from application properties.</li>
 *   <li><b>Production URL removed.</b> {@code https://api.company.com} as a
 *       hard-coded server entry would appear in the Swagger UI of local and
 *       staging environments, confusing developers into calling production.</li>
 *   <li><b>Server list built dynamically</b> based on the active Spring profile,
 *       so developers always see only the relevant server URL.</li>
 * </ul>
 *
 * <p>Add to each service's {@code application.yml}:
 * <pre>{@code
 * api:
 *   contact-email: team@yourcompany.com
 *   server-url: https://api.yourcompany.com
 * }</pre>
 */
@Configuration
public class SwaggerConfig {

    @Value("${api.contact-name:Compliance Platform Team}")
    private String contactName;

    @Value("${api.contact-email:support@complianceos.internal}")
    private String contactEmail;

    @Value("${api.server-url:http://localhost:8080}")
    private String serverUrl;

    @Value("${api.server-description:Local Development}")
    private String serverDescription;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()

                // JWT Bearer auth scheme applied globally
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token obtained from /auth/login")))

                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))

                .info(new Info()
                        .title("Compliance Platform API")
                        .version("1.0.0")
                        .description("Enterprise Compliance Microservices — " +
                                "multi-entity tracking, audit trail, and investor transparency")
                        .contact(new Contact()
                                .name(contactName)
                                .email(contactEmail))
                        .license(new License()
                                .name("Proprietary — Internal Use Only")))

                // Single server entry — environment-specific via properties
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description(serverDescription)));
    }
}
