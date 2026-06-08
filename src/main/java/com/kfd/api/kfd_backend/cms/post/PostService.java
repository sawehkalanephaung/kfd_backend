package com.kfd.api.kfd_backend.cms.post;

import com.kfd.api.kfd_backend.cms.category.PostCategory;
import com.kfd.api.kfd_backend.cms.category.PostCategoryDto;
import com.kfd.api.kfd_backend.cms.category.PostCategoryRepository;
import com.kfd.api.kfd_backend.cms.tag.Tag;
import com.kfd.api.kfd_backend.cms.tag.TagDto;
import com.kfd.api.kfd_backend.cms.tag.TagRepository;
import com.kfd.api.kfd_backend.global.exception.DuplicateResourceException;
import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostCategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    // ── Placeholder admin ID until Spring Security is wired ──
    private static final UUID MOCK_ADMIN_ID =
            UUID.fromString("d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80");

    // ── Mapper ────────────────────────────────────────────────

    private PostResponseDto toResponseDto(Post post) {
        PostCategoryDto categoryDto = null;
        if (post.getCategory() != null) {
            PostCategory cat = post.getCategory();
            categoryDto = PostCategoryDto.builder()
                    .id(cat.getId())
                    .name(cat.getName())
                    .slug(cat.getSlug())
                    .description(cat.getDescription())
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
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────

    private Post findOrThrow(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
    }

    private Set<Tag> resolveTags(List<UUID> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return new HashSet<>();
        return new HashSet<>(tagRepository.findAllById(tagIds));
    }

    private PostCategory resolveCategory(UUID categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
    }

    // ── Public API ────────────────────────────────────────────

    /**
     * Returns a paginated list of all posts (DRAFT, PUBLISHED, ARCHIVED)
     * for the admin dashboard.
     * Usage: GET /api/v1/admin/cms/posts?page=0&size=10
     */
    public Page<PostResponseDto> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(this::toResponseDto);
    }

    public PostResponseDto getPostById(UUID id) {
        return toResponseDto(findOrThrow(id));
    }

    @Transactional
    public PostResponseDto createPost(PostRequestDto dto) {
        if (dto.getSlug() != null && postRepository.existsBySlug(dto.getSlug())) {
            throw new DuplicateResourceException("Post", "slug", dto.getSlug());
        }

        OffsetDateTime now = OffsetDateTime.now();
        Post post = Post.builder()
                .title(dto.getTitle())
                .slug(dto.getSlug())
                .excerpt(dto.getExcerpt())
                .content(dto.getContent())
                .featuredImageUrl(dto.getFeaturedImageUrl())
                .authorId(MOCK_ADMIN_ID)
                .category(resolveCategory(dto.getCategoryId()))
                .tags(resolveTags(dto.getTagIds()))
                .status(dto.getStatus() != null ? dto.getStatus() : PostStatus.DRAFT)
                .publishedAt(dto.getStatus() == PostStatus.PUBLISHED ? now : null)
                .createdBy(MOCK_ADMIN_ID)
                .lastUpdatedBy(MOCK_ADMIN_ID)
                .build();

        return toResponseDto(postRepository.save(post));
    }

    @Transactional
    public PostResponseDto updatePost(UUID id, PostRequestDto dto) {
        Post post = findOrThrow(id);

        // Only validate slug uniqueness if the slug has actually changed
        if (dto.getSlug() != null && !dto.getSlug().equals(post.getSlug())
                && postRepository.existsBySlug(dto.getSlug())) {
            throw new DuplicateResourceException("Post", "slug", dto.getSlug());
        }

        post.setTitle(dto.getTitle());
        post.setSlug(dto.getSlug());
        post.setExcerpt(dto.getExcerpt());
        post.setContent(dto.getContent());
        post.setFeaturedImageUrl(dto.getFeaturedImageUrl());
        post.setCategory(resolveCategory(dto.getCategoryId()));
        post.getTags().clear();
        post.getTags().addAll(resolveTags(dto.getTagIds()));
        post.setLastUpdatedBy(MOCK_ADMIN_ID);

        if (dto.getStatus() != null) {
            // Set publishedAt timestamp the first time status transitions to PUBLISHED
            if (dto.getStatus() == PostStatus.PUBLISHED && post.getPublishedAt() == null) {
                post.setPublishedAt(OffsetDateTime.now());
            }
            post.setStatus(dto.getStatus());
        }

        return toResponseDto(postRepository.save(post));
    }

    /**
     * Soft-deletes a post by setting its status to ARCHIVED.
     * The record is preserved in the database for auditing.
     */
    @Transactional
    public void archivePost(UUID id) {
        Post post = findOrThrow(id);
        post.setStatus(PostStatus.ARCHIVED);
        post.setLastUpdatedBy(MOCK_ADMIN_ID);
        postRepository.save(post);
    }
}
