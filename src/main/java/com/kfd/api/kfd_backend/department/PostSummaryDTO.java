package com.kfd.api.kfd_backend.department;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Lightweight post summary embedded in DepartmentPublicResponseDTO. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryDTO {
    private UUID id;
    private String title;
    private String slug;
    private String excerpt;
    private String featuredImageUrl;
    private String status;
    private OffsetDateTime publishedAt;
}
