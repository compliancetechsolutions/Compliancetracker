package com.compliance.apigateway.config;

import java.util.ArrayList;
import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class SwaggerGatewayConfig {

	@Bean
	public List<GroupedOpenApi> apis() {

		List<GroupedOpenApi> groups = new ArrayList<>();

		groups.add(GroupedOpenApi.builder().group("auth-service").pathsToMatch("/auth/**").build());
		groups.add(GroupedOpenApi.builder().group("entity-service").pathsToMatch("/entity/**").build());
		groups.add(GroupedOpenApi.builder().group("compliance-service").pathsToMatch("/compliance/**").build());
		groups.add(GroupedOpenApi.builder().group("notification-service").pathsToMatch("/notification/**").build());
		groups.add(GroupedOpenApi.builder().group("audit-service").pathsToMatch("/audit/**").build());
		groups.add(GroupedOpenApi.builder().group("archive-service").pathsToMatch("/archive/**").build());
		groups.add(GroupedOpenApi.builder().group("initiator-service").pathsToMatch("/initiator/**").build());
		groups.add(GroupedOpenApi.builder().group("investor-service").pathsToMatch("/investor/**").build());
		groups.add(GroupedOpenApi.builder().group("report-service").pathsToMatch("/report/**").build());

		return groups;
	}

}
