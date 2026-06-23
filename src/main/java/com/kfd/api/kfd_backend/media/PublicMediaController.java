package com.kfd.api.kfd_backend.media;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public/media")
@RequiredArgsConstructor
public class PublicMediaController {

    private final MediaAssetRepository mediaAssetRepository;

    private MediaResponseDTO toDto(MediaAsset asset) {
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

    /**
     * GET /api/v1/public/media
     * Returns paginated media assets, optionally filtered by category and search term.
     */
    @GetMapping
    public ResponseEntity<ApiDataResponse<Page<MediaResponseDTO>>> getPublicMedia(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "newest") String sort) {

        String safeCategory = category == null ? "" : category;
        String safeSearch = search == null ? "" : search;
        
        Sort sortObj;
        switch (sort.toLowerCase()) {
            case "oldest":
                sortObj = Sort.by(Sort.Direction.ASC, "createdAt");
                break;
            case "name_asc":
                sortObj = Sort.by(Sort.Direction.ASC, "fileName");
                break;
            case "name_desc":
                sortObj = Sort.by(Sort.Direction.DESC, "fileName");
                break;
            case "newest":
            default:
                sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<MediaAsset> assets = mediaAssetRepository.searchMediaPublic(safeCategory, safeSearch, pageable);
        
        Page<MediaResponseDTO> result = assets.map(this::toDto);
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Media retrieved successfully", result));
    }

    /**
     * GET /api/v1/public/media/categories
     * Returns unique media categories along with their document counts.
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiDataResponse<List<Map<String, Object>>>> getMediaCategories() {
        List<Map<String, Object>> categories = mediaAssetRepository.countByCategory();
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Categories retrieved successfully", categories));
    }
}
