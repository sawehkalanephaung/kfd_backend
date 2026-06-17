package com.kfd.api.kfd_backend.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    /** Fetch all logs for a specific user, newest first (via Pageable sort). */
    Page<AuditLog> findByUserId(UUID userId, Pageable pageable);

    /** Fetch all logs for a specific entity type (e.g. "USER", "FAQ"), newest first. */
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);
}
