package com.kfd.api.kfd_backend.audit;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Read-only DTO returned by the Admin Audit Log API.
 */
public record AuditLogResponseDTO(
        UUID id,
        UUID userId,
        String actionType,
        String entityType,
        UUID entityId,
        String ipAddress,
        String userAgent,
        OffsetDateTime createdAt
) {}
