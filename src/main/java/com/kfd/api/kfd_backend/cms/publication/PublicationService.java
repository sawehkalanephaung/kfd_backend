package com.kfd.api.kfd_backend.cms.publication;

import com.kfd.api.kfd_backend.audit.AuditHelper;
import com.kfd.api.kfd_backend.cms.publicationcategory.PublicationCategory;
import com.kfd.api.kfd_backend.cms.publicationcategory.PublicationCategoryDto;
import com.kfd.api.kfd_backend.cms.publicationcategory.PublicationCategoryRepository;
import com.kfd.api.kfd_backend.department.Department;
import com.kfd.api.kfd_backend.department.DepartmentRepository;
import com.kfd.api.kfd_backend.global.exception.DuplicateResourceException;
import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import com.kfd.api.kfd_backend.media.MediaAsset;
import com.kfd.api.kfd_backend.media.MediaAssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final PublicationCategoryRepository categoryRepository;
    private final MediaAssetRepository mediaAssetRepository;
    private final DepartmentRepository departmentRepository;
    private final AuditHelper auditHelper;

    // ── Mapper ────────────────────────────────────────────────

    /** Exposed so public controllers can map an already-fetched entity without an extra lookup. */
    @Transactional(readOnly = true)
    public PublicationResponseDto toResponseDto(Publication publication) {
        PublicationCategoryDto categoryDto = null;
        if (publication.getCategory() != null) {
            PublicationCategory cat = publication.getCategory();
            categoryDto = PublicationCategoryDto.builder()
                    .id(cat.getId())
                    .name(cat.getName())
                    .slug(cat.getSlug())
                    .description(cat.getDescription())
                    .showInPublic(cat.isShowInPublic())
                    .build();
        }

        MediaAsset document = mediaAssetRepository.findById(publication.getDocumentId()).orElse(null);
        MediaAsset thumbnail = publication.getThumbnailId() != null
                ? mediaAssetRepository.findById(publication.getThumbnailId()).orElse(null)
                : null;

        return PublicationResponseDto.builder()
                .id(publication.getId())
                .title(publication.getTitle())
                .summary(publication.getSummary())
                .category(categoryDto)
                .publishedDate(publication.getPublishedDate())
                .issuedBy(publication.getIssuedBy())
                .departmentId(publication.getDepartment() != null ? publication.getDepartment().getId() : null)
                .language(publication.getLanguage())
                .referenceNo(publication.getReferenceNo())
                .documentId(publication.getDocumentId())
                .documentUrl(document != null ? document.getFileUrl() : null)
                .documentFileName(document != null ? document.getFileName() : null)
                .documentFileType(document != null ? document.getFileType() : null)
                .documentFileSizeKb(document != null ? document.getFileSizeKb() : null)
                .thumbnailId(publication.getThumbnailId())
                .thumbnailUrl(thumbnail != null ? thumbnail.getFileUrl() : null)
                .slug(publication.getSlug())
                .status(publication.getStatus())
                .downloadCount(publication.getDownloadCount())
                .createdAt(publication.getCreatedAt())
                .updatedAt(publication.getUpdatedAt())
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────

    private Publication findOrThrow(UUID id) {
        return publicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publication", "id", id));
    }

    private PublicationCategory resolveCategory(UUID categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("PublicationCategory", "id", categoryId));
    }

    private Department resolveDepartment(UUID departmentId) {
        if (departmentId == null) return null;
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", departmentId));
    }

    private void validateAssetExists(UUID mediaAssetId, String fieldName) {
        if (mediaAssetId != null && !mediaAssetRepository.existsById(mediaAssetId)) {
            throw new ResourceNotFoundException("MediaAsset", fieldName, mediaAssetId);
        }
    }

    // ── Public API ────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<PublicationResponseDto> getAllPublications(String search, String category, String statusStr, Pageable pageable) {
        PublicationStatus statusEnum = null;
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                statusEnum = PublicationStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid status for filtering
            }
        }
        // See PostService.getAllPosts — a blank-but-not-empty filter value would
        // otherwise empty the list instead of leaving it unfiltered.
        String normalizedCategory = (category == null || category.isBlank()) ? null : category.trim();
        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();

        return publicationRepository.searchAdminPublications(normalizedSearch, normalizedCategory, statusEnum, pageable)
                .map(this::toResponseDto);
    }

    @Transactional(readOnly = true)
    public PublicationResponseDto getPublicationById(UUID id) {
        return toResponseDto(findOrThrow(id));
    }

    @Transactional
    public PublicationResponseDto createPublication(PublicationRequestDto dto) {
        if (dto.getSlug() != null && publicationRepository.existsBySlug(dto.getSlug())) {
            throw new DuplicateResourceException("Publication", "slug", dto.getSlug());
        }
        validateAssetExists(dto.getDocumentId(), "documentId");
        validateAssetExists(dto.getThumbnailId(), "thumbnailId");

        UUID currentUserId = auditHelper.getCurrentUserId();
        Publication publication = Publication.builder()
                .title(dto.getTitle())
                .summary(dto.getSummary())
                .category(resolveCategory(dto.getCategoryId()))
                .publishedDate(dto.getPublishedDate())
                .issuedBy(dto.getIssuedBy())
                .department(resolveDepartment(dto.getDepartmentId()))
                .language(dto.getLanguage() != null ? dto.getLanguage() : "English")
                .referenceNo(dto.getReferenceNo())
                .documentId(dto.getDocumentId())
                .thumbnailId(dto.getThumbnailId())
                .slug(dto.getSlug())
                .status(dto.getStatus() != null ? dto.getStatus() : PublicationStatus.DRAFT)
                .createdBy(currentUserId)
                .lastUpdatedBy(currentUserId)
                .build();

        return toResponseDto(publicationRepository.save(publication));
    }

    @Transactional
    public PublicationResponseDto updatePublication(UUID id, PublicationRequestDto dto) {
        Publication publication = findOrThrow(id);

        if (dto.getSlug() != null && !dto.getSlug().equals(publication.getSlug())
                && publicationRepository.existsBySlug(dto.getSlug())) {
            throw new DuplicateResourceException("Publication", "slug", dto.getSlug());
        }
        validateAssetExists(dto.getDocumentId(), "documentId");
        validateAssetExists(dto.getThumbnailId(), "thumbnailId");

        publication.setTitle(dto.getTitle());
        publication.setSummary(dto.getSummary());
        publication.setCategory(resolveCategory(dto.getCategoryId()));
        publication.setPublishedDate(dto.getPublishedDate());
        publication.setIssuedBy(dto.getIssuedBy());
        publication.setDepartment(resolveDepartment(dto.getDepartmentId()));
        if (dto.getLanguage() != null) publication.setLanguage(dto.getLanguage());
        publication.setReferenceNo(dto.getReferenceNo());
        publication.setDocumentId(dto.getDocumentId());
        publication.setThumbnailId(dto.getThumbnailId());
        publication.setSlug(dto.getSlug());
        publication.setLastUpdatedBy(auditHelper.getCurrentUserId());

        if (dto.getStatus() != null) {
            publication.setStatus(dto.getStatus());
        }

        return toResponseDto(publicationRepository.save(publication));
    }

    /**
     * Soft-deletes a publication by setting its status to ARCHIVED.
     * If the publication is already ARCHIVED, it permanently hard-deletes the record.
     * @return true if permanently deleted, false if archived (soft-deleted)
     */
    @Transactional
    public boolean deleteOrArchivePublication(UUID id) {
        Publication publication = findOrThrow(id);
        if (publication.getStatus() == PublicationStatus.ARCHIVED) {
            publicationRepository.delete(publication);
            return true;
        } else {
            publication.setStatus(PublicationStatus.ARCHIVED);
            publication.setLastUpdatedBy(auditHelper.getCurrentUserId());
            publicationRepository.save(publication);
            return false;
        }
    }

    /** Called from the public download endpoint — increments the analytics counter. */
    @Transactional
    public PublicationResponseDto registerDownload(UUID id) {
        Publication publication = publicationRepository.findById(id)
                .filter(p -> p.getStatus() == PublicationStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Publication", "id", id));
        publication.setDownloadCount(publication.getDownloadCount() + 1);
        return toResponseDto(publicationRepository.save(publication));
    }
}
