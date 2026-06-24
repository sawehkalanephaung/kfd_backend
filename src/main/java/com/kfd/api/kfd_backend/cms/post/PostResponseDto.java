package com.kfd.api.kfd_backend.cms.post;

import com.kfd.api.kfd_backend.cms.category.PostCategoryDto;
import com.kfd.api.kfd_backend.cms.tag.TagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Rich outgoing response DTO for GET operations.
 * Includes fully nested category and tags objects so the frontend
 * gets everything it needs in a single call.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private UUID id;
    private String title;
    private String slug;
    private String excerpt;
    private String content;
    private String featuredImageUrl;
    private UUID authorId;
    private PostCategoryDto category;
    private List<TagDto> tags;
    private PostStatus status;
    private Integer viewCount;
    private OffsetDateTime publishedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private java.util.Map<String, Object> metadata;
}
