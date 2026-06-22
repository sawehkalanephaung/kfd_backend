package com.kfd.api.kfd_backend.settings.footer;

import java.util.UUID;

public record FooterLinkResponseDTO(
        UUID id,
        String label,
        String url,
        Integer displayOrder,
        Boolean isActive
) {
    public static FooterLinkResponseDTO from(FooterLink entity) {
        return new FooterLinkResponseDTO(
                entity.getId(),
                entity.getLabel(),
                entity.getUrl(),
                entity.getDisplayOrder(),
                entity.getIsActive()
        );
    }
}
