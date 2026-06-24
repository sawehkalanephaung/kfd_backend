package com.kfd.api.kfd_backend.cms.post;

import com.kfd.api.kfd_backend.cms.category.PostCategoryDto;
import com.kfd.api.kfd_backend.cms.tag.TagDto;
import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/posts")
@RequiredArgsConstructor
public class PublicPostController {

    private final PostRepository postRepository;

    /**
     * GET /api/v1/public/posts?page=0&size=9&categorySlug=field-update
     * Returns paginated PUBLISHED posts, optionally filtered by categorySlug.
     */
    @GetMapping
    public ResponseEntity<ApiDataResponse<Page<PostResponseDto>>> getPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String categorySlug) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts;

        if (categorySlug != null && !categorySlug.isBlank()) {
            posts = postRepository.findByStatusAndCategorySlugOrderByPublishedAtDesc(
                    PostStatus.PUBLISHED, categorySlug, pageable);
        } else {
            posts = postRepository.findByStatusOrderByPublishedAtDesc(PostStatus.PUBLISHED, pageable);
        }

        Page<PostResponseDto> result = posts.map(this::toDto);
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Posts retrieved successfully", result));
    }

    /**
     * GET /api/v1/public/posts/{slug}
     * Returns a single PUBLISHED post by slug.
     */
    @GetMapping("/{slug}")
    public ResponseEntity<ApiDataResponse<PostDetailDto>> getPostBySlug(@PathVariable String slug) {
        Post post = postRepository.findBySlugAndStatus(slug, PostStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "slug", slug));

        // Fetch related posts: same category, up to 3, excluding current
        List<PostResponseDto> related = List.of();
        if (post.getCategory() != null) {
            related = postRepository.findTop3ByStatusAndCategoryIdAndIdNotOrderByPublishedAtDesc(
                            PostStatus.PUBLISHED, post.getCategory().getId(), post.getId())
                    .stream().map(this::toDto).toList();
        }

        PostDetailDto detail = PostDetailDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .excerpt(post.getExcerpt())
                .content(post.getContent())
                .featuredImageUrl(post.getFeaturedImageUrl())
                .authorId(post.getAuthorId())
                .category(post.getCategory() != null ? PostCategoryDto.builder()
                        .id(post.getCategory().getId())
                        .name(post.getCategory().getName())
                        .slug(post.getCategory().getSlug())
                        .description(post.getCategory().getDescription())
                        .build() : null)
                .tags(post.getTags().stream().map(t -> TagDto.builder()
                        .id(t.getId()).name(t.getName()).slug(t.getSlug()).build()).toList())
                .status(post.getStatus())
                .viewCount(post.getViewCount())
                .publishedAt(post.getPublishedAt())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .metadata(post.getMetadata())
                .relatedPosts(related)
                .build();

        return ResponseEntity.ok(new ApiDataResponse<>(200, "Post retrieved successfully", detail));
    }

    // ── Mapper ─────────────────────────────────────────────────

    private PostResponseDto toDto(Post post) {
        PostCategoryDto categoryDto = null;
        if (post.getCategory() != null) {
            categoryDto = PostCategoryDto.builder()
                    .id(post.getCategory().getId())
                    .name(post.getCategory().getName())
                    .slug(post.getCategory().getSlug())
                    .description(post.getCategory().getDescription())
                    .build();
        }

        List<TagDto> tagDtos = post.getTags().stream()
                .map(tag -> TagDto.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .slug(tag.getSlug())
                        .build())
                .toList();

        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .excerpt(post.getExcerpt())
                .content(post.getContent())
                .featuredImageUrl(post.getFeaturedImageUrl())
                .authorId(post.getAuthorId())
                .category(categoryDto)
                .tags(tagDtos)
                .status(post.getStatus())
                .viewCount(post.getViewCount())
                .publishedAt(post.getPublishedAt())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .metadata(post.getMetadata())
                .build();
    }
}
