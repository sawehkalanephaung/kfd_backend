package com.kfd.api.kfd_backend.settings.social;

import java.time.LocalDateTime;
import java.util.UUID;

public record SocialMediaLinkResponseDTO(
        UUID id,
        String platformName,
        String url,
        Integer displayOrder,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static SocialMediaLinkResponseDTO fromEntity(SocialMediaLink link) {
        if (link == null) {
            return null;
        }
        return new SocialMediaLinkResponseDTO(
                link.getId(),
                link.getPlatformName(),
                link.getUrl(),
                link.getDisplayOrder(),
                link.getIsActive(),
                link.getCreatedAt(),
                link.getUpdatedAt()
        );
    }
}
