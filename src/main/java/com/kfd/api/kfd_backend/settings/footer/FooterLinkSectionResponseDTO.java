package com.kfd.api.kfd_backend.settings.footer;

import java.util.List;
import java.util.UUID;

public record FooterLinkSectionResponseDTO(
        UUID id,
        String title,
        Integer displayOrder,
        Boolean isActive,
        List<FooterLinkResponseDTO> links
) {
    public static FooterLinkSectionResponseDTO from(FooterLinkSection entity) {
        List<FooterLinkResponseDTO> linkDtos = entity.getLinks().stream()
                .map(FooterLinkResponseDTO::from)
                .toList();
        return new FooterLinkSectionResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDisplayOrder(),
                entity.getIsActive(),
                linkDtos
        );
    }

    /**
     * For public API: only include active links.
     */
    public static FooterLinkSectionResponseDTO fromPublic(FooterLinkSection entity) {
        List<FooterLinkResponseDTO> linkDtos = entity.getLinks().stream()
                .filter(FooterLink::getIsActive)
                .map(FooterLinkResponseDTO::from)
                .toList();
        return new FooterLinkSectionResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDisplayOrder(),
                entity.getIsActive(),
                linkDtos
        );
    }
}
