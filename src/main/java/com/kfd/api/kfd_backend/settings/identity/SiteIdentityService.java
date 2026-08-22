package com.kfd.api.kfd_backend.settings.identity;

import com.kfd.api.kfd_backend.global.exception.ResourceInUseException;
import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Business logic for the organization's site identity.
 *
 * Follows the singleton-settings pattern established by {@code ContactSettingsService}:
 * the table supports full CRUD, but in practice one record is the site's identity and
 * the public read always resolves to the first one.
 */
@Service
@RequiredArgsConstructor
public class SiteIdentityService {

    /** Used when no record exists at all, so the public site can still render. */
    static final String DEFAULT_ORGANIZATION_NAME = "Kawthoolei Forestry Department";

    private final SiteIdentityRepository repository;

    @Transactional(readOnly = true)
    public List<SiteIdentityResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(SiteIdentityResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * The record the public site renders.
     *
     * Never throws: branding is not optional, so an empty table falls back to a
     * transient default rather than failing the page. This is a read-only path,
     * so nothing is persisted as a side effect of a public request.
     */
    @Transactional(readOnly = true)
    public SiteIdentityResponseDTO getSiteIdentity() {
        return repository.findAll().stream()
                .findFirst()
                .map(SiteIdentityResponseDTO::from)
                .orElseGet(() -> new SiteIdentityResponseDTO(
                        null, DEFAULT_ORGANIZATION_NAME, null, null, null));
    }

    @Transactional(readOnly = true)
    public SiteIdentityResponseDTO getById(UUID id) {
        return repository.findById(id)
                .map(SiteIdentityResponseDTO::from)
                .orElseThrow(() -> new ResourceNotFoundException("SiteIdentity", "id", id.toString()));
    }

    @Transactional
    public SiteIdentityResponseDTO create(SiteIdentityRequestDTO dto) {
        SiteIdentity entity = SiteIdentity.builder()
                .organizationName(dto.organizationName())
                .tagline(dto.tagline())
                .logoUrl(dto.logoUrl())
                .footerCopyright(dto.footerCopyright())
                .build();

        return SiteIdentityResponseDTO.from(repository.save(entity));
    }

    /**
     * Updates the singleton record, creating it if the table is empty so the admin
     * screen works on a fresh database without a separate "create" step.
     */
    @Transactional
    public SiteIdentityResponseDTO updateSiteIdentity(SiteIdentityRequestDTO dto) {
        SiteIdentity entity = repository.findAll().stream()
                .findFirst()
                .orElseGet(SiteIdentity::new);

        applyChanges(entity, dto);
        return SiteIdentityResponseDTO.from(repository.save(entity));
    }

    @Transactional
    public SiteIdentityResponseDTO update(UUID id, SiteIdentityRequestDTO dto) {
        SiteIdentity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SiteIdentity", "id", id.toString()));

        applyChanges(entity, dto);
        return SiteIdentityResponseDTO.from(repository.save(entity));
    }

    /**
     * Deletes an identity record.
     *
     * Business rule: the site must always have an identity, so removing the last
     * remaining record is refused rather than leaving the public site unbranded.
     * Mirrors the "last Super Admin" guard in {@code UserService}.
     */
    @Transactional
    public void delete(UUID id) {
        SiteIdentity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SiteIdentity", "id", id.toString()));

        if (repository.count() <= 1) {
            throw new ResourceInUseException(
                    "Cannot delete the only site identity — the public site would have no "
                            + "organization name. Update it instead, or add another first.");
        }

        repository.delete(entity);
    }

    private void applyChanges(SiteIdentity entity, SiteIdentityRequestDTO dto) {
        entity.setOrganizationName(dto.organizationName());
        entity.setTagline(dto.tagline());
        entity.setLogoUrl(dto.logoUrl());
        entity.setFooterCopyright(dto.footerCopyright());
    }
}
