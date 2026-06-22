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
 * Extended post DTO for the public detail page.
 * Includes related posts for the "Related Topics" section.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailDto {
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
    /** Related posts in the same category (max 3) */
    private List<PostResponseDto> relatedPosts;
}
