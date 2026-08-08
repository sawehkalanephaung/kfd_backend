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

    private final MediaService mediaService;

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

        Sort sortObj = switch (sort.toLowerCase()) {
            case "oldest"    -> Sort.by(Sort.Direction.ASC,  "createdAt");
            case "name_asc"  -> Sort.by(Sort.Direction.ASC,  "fileName");
            case "name_desc" -> Sort.by(Sort.Direction.DESC, "fileName");
            default          -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<MediaResponseDTO> result = mediaService.getPublicMedia(category, search, pageable);
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Media retrieved successfully", result));
    }

    /**
     * GET /api/v1/public/media/categories
     * Returns unique media categories along with their document counts.
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiDataResponse<List<Map<String, Object>>>> getMediaCategories() {
        List<Map<String, Object>> categories = mediaService.getMediaCategories();
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Categories retrieved successfully", categories));
    }
}
