package com.kfd.api.kfd_backend.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO {
    private UUID id;
    private String slug;
    private String title;
    private String content;
    private UUID heroImageId;
    private String heroImageUrl;
    private List<UUID> sliderImageIds;
    private List<String> sliderImageUrls;
    private String status;
    private UUID createdBy;
    private UUID lastUpdatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
