package com.compliance.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication(scanBasePackages = "com.compliance")
@EnableDiscoveryClient
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")  
public class AuthServiceApplication {

    public static void main(String[] args) {
    	//BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        //System.out.println("HASH = " + encoder.encode("Password@123#"));
        SpringApplication.run(AuthServiceApplication.class, args);
    }

}


