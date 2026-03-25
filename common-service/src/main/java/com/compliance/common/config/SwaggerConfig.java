package com.compliance.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
	 @Bean
	    public OpenAPI customOpenAPI() {

	        return new OpenAPI()

	                // JWT SECURITY
	                .components(new Components()
	                        .addSecuritySchemes("bearerAuth",
	                                new SecurityScheme()
	                                        .type(SecurityScheme.Type.HTTP)
	                                        .scheme("bearer")   
	                                        .bearerFormat("JWT")
	                        )
	                )

	                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))

	                // 📄 API INFO
	                .info(new Info()
	                        .title("Compliance Platform API")
	                        .version("1.0")
	                        .description("Enterprise Compliance Microservices APIs")

	                        .contact(new Contact()
	                                .name("Compliance Team")
	                                .email("compliancetech@rediffmail.com")
	                        )

	                        .license(new License()
	                                .name("Internal Use Only")
	                                .url("https://company.com/license")
	                        )
	                )

	                //  SERVER INFO
	                .servers(List.of(
	                        new Server().url("http://localhost:8080").description("Local"),
	                        new Server().url("https://api.company.com").description("Production")
	                ));
	    }

}
