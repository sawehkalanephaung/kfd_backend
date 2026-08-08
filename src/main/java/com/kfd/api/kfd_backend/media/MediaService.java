package com.kfd.api.kfd_backend.media;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service layer for media asset business logic.
 * Both AdminMediaController and PublicMediaController go through here.
 */
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaAssetRepository mediaAssetRepository;

    // ── Shared mapper ─────────────────────────────────────────────

    public MediaResponseDTO toDto(MediaAsset asset) {
        return MediaResponseDTO.builder()
                .id(asset.getId())
                .fileName(asset.getFileName())
                .fileUrl(asset.getFileUrl())
                .fileType(asset.getFileType())
                .fileSizeKb(asset.getFileSizeKb())
                .mediaCategory(asset.getMediaCategory())
                .language(asset.getLanguage())
                .departmentId(asset.getDepartmentId())
                .uploadedBy(asset.getUploadedBy())
                .createdAt(asset.getCreatedAt())
                .build();
    }

    // ── Public queries ────────────────────────────────────────────

    /**
     * Returns paginated media assets, optionally filtered by category and search term.
     * Intended for public-facing use (no sensitive fields exposed).
     */
    public Page<MediaResponseDTO> getPublicMedia(String category, String search, Pageable pageable) {
        String safeCategory = category == null ? "" : category;
        String safeSearch = search == null ? "" : search;
        return mediaAssetRepository.searchMediaPublic(safeCategory, safeSearch, pageable).map(this::toDto);
    }

    /**
     * Returns unique media categories with their document counts for the public filter bar.
     */
    public List<Map<String, Object>> getMediaCategories() {
        return mediaAssetRepository.countByCategory();
    }
}
