package com.kfd.api.kfd_backend.audit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service responsible for writing audit log entries.
 *
 * All methods are @Async — they run in a background thread so they
 * never slow down the main API response.
 *
 * Usage example in any controller or service:
 *   auditLogService.log(userId, "CREATE", "FAQ", faq.getId(), httpRequest);
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Logs any action asynchronously.
     *
     * @param userId     The UUID of the user who performed the action (may be null for anonymous events).
     * @param actionType e.g. LOGIN, CREATE, UPDATE, DELETE
     * @param entityType e.g. USER, FAQ, TEAM_MEMBER, DEPARTMENT, PAGE, ROLE
     * @param entityId   The UUID of the affected record (null for login events).
     * @param request    The HttpServletRequest to extract IP and User-Agent from.
     */
    @Async
    public void log(UUID userId, String actionType, String entityType, UUID entityId, HttpServletRequest request) {
        try {
            AuditLog entry = AuditLog.builder()
                    .userId(userId)
                    .actionType(actionType)
                    .entityType(entityType)
                    .entityId(entityId)
                    .ipAddress(extractIp(request))
                    .userAgent(extractUserAgent(request))
                    .build();
            auditLogRepository.save(entry);
        } catch (Exception e) {
            // Audit logging must NEVER crash the main request — only log the error.
            log.error("Failed to write audit log entry: action={}, entity={}, id={}, error={}",
                    actionType, entityType, entityId, e.getMessage());
        }
    }

    /**
     * Convenience overload for actions not tied to a specific entity record (e.g. LOGIN).
     */
    @Async
    public void log(UUID userId, String actionType, String entityType, HttpServletRequest request) {
        log(userId, actionType, entityType, null, request);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    /**
     * Extracts the real client IP, respecting common reverse-proxy headers.
     */
    private String extractIp(HttpServletRequest request) {
        if (request == null) return null;
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            // X-Forwarded-For can be a comma-separated list; the first IP is the original client.
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String extractUserAgent(HttpServletRequest request) {
        if (request == null) return null;
        String ua = request.getHeader("User-Agent");
        // Truncate to 512 chars to match the DB column length.
        return (ua != null && ua.length() > 512) ? ua.substring(0, 512) : ua;
    }
}
