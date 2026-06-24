package com.kfd.api.kfd_backend.cms.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Incoming request body for POST / and PUT /{id}.
 * The frontend sends category and tags as IDs; the service resolves them to entities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    private String title;
    private String slug;
    private String excerpt;
    private String content;
    private String featuredImageUrl;
    private UUID categoryId;
    private UUID lastUpdatedBy;

    private java.util.Map<String, Object> metadata;
    private List<UUID> tagIds;
    private PostStatus status;
}
