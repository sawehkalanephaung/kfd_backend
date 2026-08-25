package com.kfd.api.kfd_backend.audit;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Admin-only endpoint for viewing audit logs.
 * Restricted to ROLE_SUPER_ADMIN only — audit logs contain sensitive security data.
 *
 * GET /api/v1/admin/audit-logs               → all logs (paginated)
 * GET /api/v1/admin/audit-logs?userId={uuid} → filter by user
 * GET /api/v1/admin/audit-logs?entityType={type} → filter by entity type
 */
@RestController
@RequestMapping("/api/v1/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('view_analytics') or hasAuthority('ROLE_SUPER_ADMIN')")
public class AdminAuditLogController {

    private final AuditLogRepository auditLogRepository;

    /**
     * Get all audit logs, paginated.
     * Supports filtering by userId or entityType via query params.
     *
     * Examples:
     *   GET /api/v1/admin/audit-logs?page=0&size=20&sort=createdAt,desc
     *   GET /api/v1/admin/audit-logs?userId=uuid-here&page=0&size=20
     *   GET /api/v1/admin/audit-logs?entityType=USER&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<ApiDataResponse<Page<AuditLogResponseDTO>>> getLogs(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String entityType,
            Pageable pageable) {

        Page<AuditLog> logs;

        if (userId != null) {
            logs = auditLogRepository.findByUserId(userId, pageable);
        } else if (entityType != null && !entityType.isBlank()) {
            logs = auditLogRepository.findByEntityType(entityType.toUpperCase(), pageable);
        } else {
            logs = auditLogRepository.findAll(pageable);
        }

        Page<AuditLogResponseDTO> response = logs.map(this::toDto);
        return ResponseEntity.ok(new ApiDataResponse<>(HttpStatus.OK.value(), "Audit logs retrieved successfully", response));
    }

    /**
     * Get a single audit log entry by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiDataResponse<AuditLogResponseDTO>> getById(@PathVariable UUID id) {
        AuditLog log = auditLogRepository.findById(id)
                .orElseThrow(() -> new com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException("AuditLog", "id", id));
        return ResponseEntity.ok(new ApiDataResponse<>(HttpStatus.OK.value(), "Audit log retrieved", toDto(log)));
    }

    // ─── Mapper ──────────────────────────────────────────────────────────────────

    private AuditLogResponseDTO toDto(AuditLog log) {
        return new AuditLogResponseDTO(
                log.getId(),
                log.getUserId(),
                log.getActionType(),
                log.getEntityType(),
                log.getEntityId(),
                log.getIpAddress(),
                log.getUserAgent(),
                log.getCreatedAt()
        );
    }
}
