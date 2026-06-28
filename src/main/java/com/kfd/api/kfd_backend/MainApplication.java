package com.kfd.api.kfd_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * KFD Backend entry point.
 *
 * @EnableJpaAuditing  – activates Spring Data JPA Auditing; the auditorProvider
 *                       bean (AuditorAwareImpl) supplies the current user's UUID
 *                       for @CreatedBy / @LastModifiedBy fields.
 *
 * @EnableAsync        – allows @Async methods (e.g. AuditLogService.log()) to
 *                       run in a background thread pool, never blocking API responses.
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableAsync
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
