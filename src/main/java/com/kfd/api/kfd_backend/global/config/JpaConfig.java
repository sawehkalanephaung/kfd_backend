package com.kfd.api.kfd_backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Central JPA + async configuration.
 *
 * @EnableJpaAuditing  – wires AuditorAwareImpl (bean name "auditorProvider") to
 *                       auto-populate @CreatedBy / @LastModifiedBy on entities.
 * @EnableAsync        – allows @Async methods (e.g. AuditLogService.log()) to run
 *                       in a background thread pool, never blocking API responses.
 */
@Configuration
@EnableAsync
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {
}
