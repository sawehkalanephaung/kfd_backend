package com.kfd.api.kfd_backend.settings.identity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SiteIdentityService {

    private final SiteIdentityRepository repository;

    /**
     * Returns the singleton site identity row.
     * If none exists, creates a default one.
     */
    public SiteIdentityResponseDTO getSiteIdentity() {
        SiteIdentity entity = repository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    SiteIdentity defaults = SiteIdentity.builder()
                            .organizationName("Kawthoolei Forest Department")
                            .tagline("Official Government Portal")
                            .footerCopyright("© 2025 Kawthoolei Forest Department. All rights reserved.")
                            .build();
                    return repository.save(defaults);
                });
        return SiteIdentityResponseDTO.from(entity);
    }

    /**
     * Updates the singleton site identity row.
     */
    public SiteIdentityResponseDTO updateSiteIdentity(SiteIdentityRequestDTO dto) {
        SiteIdentity entity = repository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    SiteIdentity defaults = SiteIdentity.builder().build();
                    return repository.save(defaults);
                });

        if (dto.organizationName() != null) {
            entity.setOrganizationName(dto.organizationName());
        }
        if (dto.tagline() != null) {
            entity.setTagline(dto.tagline());
        }
        if (dto.logoUrl() != null) {
            entity.setLogoUrl(dto.logoUrl());
        }
        if (dto.footerCopyright() != null) {
            entity.setFooterCopyright(dto.footerCopyright());
        }

        return SiteIdentityResponseDTO.from(repository.save(entity));
    }
}
