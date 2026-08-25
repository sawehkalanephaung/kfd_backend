package com.kfd.api.kfd_backend.settings.identity;

import java.util.UUID;

public record SiteIdentityResponseDTO(
        UUID id,
        String organizationName,
        String organizationNameKaren,
        String tagline,
        String logoUrl,
        String footerCopyright
) {
    public static SiteIdentityResponseDTO from(SiteIdentity entity) {
        return new SiteIdentityResponseDTO(
                entity.getId(),
                entity.getOrganizationName(),
                entity.getOrganizationNameKaren(),
                entity.getTagline(),
                entity.getLogoUrl(),
                entity.getFooterCopyright()
        );
    }
}
