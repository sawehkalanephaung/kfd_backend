package com.kfd.api.kfd_backend.cms.publication;

import com.kfd.api.kfd_backend.cms.publicationcategory.PublicationCategoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Rich outgoing response DTO for GET operations.
 * Resolves documentId/thumbnailId to their media_assets URLs so the frontend
 * gets everything it needs in a single call, the same way PostResponseDto
 * resolves sliderImageIds to sliderImageUrls.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicationResponseDto {
    private UUID id;
    private String title;
    private String summary;
    private PublicationCategoryDto category;
    private LocalDate publishedDate;
    private String issuedBy;
    private UUID departmentId;
    private String language;
    private String referenceNo;

    private UUID documentId;
    private String documentUrl;
    private String documentFileName;
    private String documentFileType;
    private Integer documentFileSizeKb;

    private UUID thumbnailId;
    private String thumbnailUrl;

    private String slug;
    private PublicationStatus status;
    private Integer downloadCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
