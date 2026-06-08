package com.compliance.compliance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Compliance Service entry point.
 *
 * <p><b>FIXES:</b>
 * <ul>
 *   <li>{@code @EnableJpaAuditing} — required for {@code @CreatedDate} /
 *       {@code @LastModifiedDate} / {@code @CreatedBy} in {@code BaseEntity}
 *       to be populated automatically. Without this, all audit fields remain null.</li>
 *   <li>{@code @EnableScheduling} — required for {@code @Scheduled} methods in
 *       {@link com.compliance.compliance.scheduler.ComplianceScheduler} to fire.
 *       Adding it to the scheduler component alone is not sufficient in all
 *       Spring Boot configurations; declaring it on the main application class
 *       guarantees it is processed.</li>
 * </ul>
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class ComplianceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComplianceServiceApplication.class, args);
    }
}