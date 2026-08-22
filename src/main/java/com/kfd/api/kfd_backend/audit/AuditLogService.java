package com.kfd.api.kfd_backend.audit;

import com.kfd.api.kfd_backend.global.web.ClientIpResolver;
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

    /** Dedicated audit logger — routes to kfd-audit.log via logback-spring.xml */
    private static final org.slf4j.Logger AUDIT_LOG = org.slf4j.LoggerFactory.getLogger("AUDIT");

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
            String ip = extractIp(request);
            String ua = extractUserAgent(request);

            AuditLog entry = AuditLog.builder()
                    .userId(userId)
                    .actionType(actionType)
                    .entityType(entityType)
                    .entityId(entityId)
                    .ipAddress(ip)
                    .userAgent(ua)
                    .build();
            auditLogRepository.save(entry);

            // Emit structured audit log line to kfd-audit.log
            AUDIT_LOG.info("action={} | entity={} | entityId={} | userId={} | ip={}",
                    actionType, entityType, entityId, userId, ip);
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

    /**
     * Overload for public/anonymous events where there is no HttpServletRequest
     * available (e.g. inquiry submissions logged via SLF4J only).
     * This variant does NOT persist to the DB — it only writes to the audit log file.
     *
     * @param actionType  e.g. SUBMIT
     * @param entityType  e.g. INQUIRY
     * @param description Free-text detail (e.g. sender email, inquiry type)
     */
    public void logAnonymous(String actionType, String entityType, String description) {
        AUDIT_LOG.info("action={} | entity={} | userId=anonymous | detail={}",
                actionType, entityType, description);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    /**
     * Extracts the real client IP, respecting common reverse-proxy headers.
     */
    private String extractIp(HttpServletRequest request) {
        return ClientIpResolver.resolve(request);
    }

    private String extractUserAgent(HttpServletRequest request) {
        if (request == null) return null;
        String ua = request.getHeader("User-Agent");
        // Truncate to 512 chars to match the DB column length.
        return (ua != null && ua.length() > 512) ? ua.substring(0, 512) : ua;
    }
}
