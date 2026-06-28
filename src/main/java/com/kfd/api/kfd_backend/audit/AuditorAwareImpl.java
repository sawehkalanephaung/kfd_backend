package com.kfd.api.kfd_backend.audit;

import com.kfd.api.kfd_backend.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Provides the currently-authenticated user's UUID to Spring Data JPA Auditing.
 *
 * Automatically populates @CreatedBy and @LastModifiedBy fields on any entity
 * that uses @EntityListeners(AuditingEntityListener.class).
 */
@Slf4j
@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        if (auth.getPrincipal() instanceof User user) {
            return Optional.of(user.getId());
        }
        return Optional.empty();
    }
}
