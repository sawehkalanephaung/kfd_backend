package com.kfd.api.kfd_backend.cms.publication;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/publications")
@RequiredArgsConstructor
public class PublicPublicationController {

    private final PublicationRepository publicationRepository;
    private final PublicationService publicationService;

    /**
     * GET /api/v1/public/publications?page=0&size=9&categorySlug=reports
     * Returns paginated PUBLISHED publications, optionally filtered by categorySlug.
     */
    @GetMapping
    public ResponseEntity<ApiDataResponse<Page<PublicationResponseDto>>> getPublishedPublications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String categorySlug) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PublicationResponseDto> result;

        if (categorySlug != null && !categorySlug.isBlank()) {
            result = publicationRepository.findByStatusAndCategorySlugOrderByPublishedDateDesc(
                            PublicationStatus.PUBLISHED, categorySlug, pageable)
                    .map(publicationService::toResponseDto);
        } else {
            result = publicationRepository.findByStatusOrderByPublishedDateDesc(PublicationStatus.PUBLISHED, pageable)
                    .map(publicationService::toResponseDto);
        }

        return ResponseEntity.ok(new ApiDataResponse<>(200, "Publications retrieved successfully", result));
    }

    /**
     * GET /api/v1/public/publications/{slug}
     * Returns a single PUBLISHED publication by slug.
     */
    @GetMapping("/{slug}")
    public ResponseEntity<ApiDataResponse<PublicationResponseDto>> getPublicationBySlug(@PathVariable String slug) {
        Publication publication = publicationRepository.findBySlugAndStatus(slug, PublicationStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Publication", "slug", slug));
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Publication retrieved successfully",
                publicationService.toResponseDto(publication)));
    }

    /**
     * GET /api/v1/public/publications/{slug}/download
     * Increments the download counter, then redirects to the underlying document's file URL.
     */
    @GetMapping("/{slug}/download")
    public ResponseEntity<Void> downloadPublication(@PathVariable String slug) {
        Publication publication = publicationRepository.findBySlugAndStatus(slug, PublicationStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Publication", "slug", slug));

        PublicationResponseDto updated = publicationService.registerDownload(publication.getId());
        String documentUrl = updated.getDocumentUrl();
        if (documentUrl == null) {
            throw new ResourceNotFoundException("MediaAsset", "publicationId", publication.getId());
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, documentUrl)
                .build();
    }
}
