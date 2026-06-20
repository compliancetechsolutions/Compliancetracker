package com.compliance.entity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "com.compliance")
@EnableDiscoveryClient

public class EntityServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(EntityServiceApplication.class, args);
  }
}