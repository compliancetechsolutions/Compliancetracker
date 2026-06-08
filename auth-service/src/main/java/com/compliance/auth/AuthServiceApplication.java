package com.compliance.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.compliance")
@EnableDiscoveryClient
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")

@EntityScan(basePackages = {

		"com.compliance.auth",

		"com.compliance.common.kafka.event" })

@EnableJpaRepositories(basePackages = {

		"com.compliance.auth.repository",

		"com.compliance.common.kafka.repository" })

public class AuthServiceApplication {

	public static void main(String[] args) {

		// System.out.println( new BCryptPasswordEncoder() .encode("admin123") );

		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
