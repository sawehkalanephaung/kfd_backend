package com.kfd.api.kfd_backend.settings.identity;

public record SiteIdentityRequestDTO(
        String organizationName,
        String tagline,
        String logoUrl,
        String footerCopyright
) {}
