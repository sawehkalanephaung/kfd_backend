package com.kfd.api.kfd_backend.settings.newsletter;

import java.time.LocalDateTime;
import java.util.UUID;

public record NewsletterSubscriberResponseDTO(
        UUID id,
        String email,
        Boolean isActive,
        LocalDateTime subscribedAt,
        LocalDateTime unsubscribedAt
) {
    public static NewsletterSubscriberResponseDTO from(NewsletterSubscriber entity) {
        return new NewsletterSubscriberResponseDTO(
                entity.getId(),
                entity.getEmail(),
                entity.getIsActive(),
                entity.getSubscribedAt(),
                entity.getUnsubscribedAt()
        );
    }
}
