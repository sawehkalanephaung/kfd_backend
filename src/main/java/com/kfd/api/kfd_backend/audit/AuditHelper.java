package com.kfd.api.kfd_backend.audit;

import com.kfd.api.kfd_backend.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Centralized helper for extracting the current authenticated user's UUID.
 *
 * Eliminates the repeated private resolveUserId(Authentication auth) helpers
 * that were duplicated across multiple controllers.
 *
 * Usage in a controller:
 * <pre>
 *   UUID actorId = AuditHelper.getCurrentUserId(); // reads from SecurityContext
 *   auditLogService.log(actorId, "CREATE", "FAQ", faq.getId(), request);
 * </pre>
 *
 * Or inject as a Spring bean:
 * <pre>
 *   private final AuditHelper auditHelper;
 *   auditHelper.getCurrentUserId();
 * </pre>
 */
@Component
public class AuditHelper {

    /**
     * Returns the UUID of the currently authenticated user,
     * or null if the request is unauthenticated / anonymous.
     */
    public UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        if (auth.getPrincipal() instanceof User user) {
            return user.getId();
        }
        return null;
    }
}
