package com.kfd.api.kfd_backend.cms.post;

import com.kfd.api.kfd_backend.audit.AuditHelper;
import com.kfd.api.kfd_backend.cms.category.PostCategory;
import com.kfd.api.kfd_backend.cms.category.PostCategoryDto;
import com.kfd.api.kfd_backend.cms.category.PostCategoryRepository;
import com.kfd.api.kfd_backend.department.Department;
import com.kfd.api.kfd_backend.department.DepartmentRepository;
import com.kfd.api.kfd_backend.cms.tag.Tag;
import com.kfd.api.kfd_backend.cms.tag.TagDto;
import com.kfd.api.kfd_backend.cms.tag.TagRepository;
import com.kfd.api.kfd_backend.media.MediaAssetRepository;
import com.kfd.api.kfd_backend.media.MediaAsset;
import com.kfd.api.kfd_backend.global.exception.DuplicateResourceException;
import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
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
    private final MediaAssetRepository mediaAssetRepository;
    private final DepartmentRepository departmentRepository;
    private final AuditHelper auditHelper;

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

        java.util.List<String> sliderImageUrls = new java.util.ArrayList<>();
        if (post.getSliderImageIds() != null && !post.getSliderImageIds().isEmpty()) {
            sliderImageUrls = mediaAssetRepository.findAllById(post.getSliderImageIds())
                    .stream()
                    .map(MediaAsset::getFileUrl)
                    .collect(java.util.stream.Collectors.toList());
        }

        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .excerpt(post.getExcerpt())
                .content(post.getContent())
                .featuredImageUrl(post.getFeaturedImageUrl())
                .sliderImageIds(post.getSliderImageIds())
                .sliderImageUrls(sliderImageUrls)
                .authorId(post.getAuthorId())
                .departmentId(post.getDepartment() != null ? post.getDepartment().getId() : null)
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

    private Department resolveDepartment(UUID departmentId) {
        if (departmentId == null) return null;
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", departmentId));
    }

    // ── Public API ────────────────────────────────────────────

    /**
     * Returns a paginated list of all posts (DRAFT, PUBLISHED, ARCHIVED)
     * for the admin dashboard, with optional filtering.
     * Usage: GET /api/v1/admin/cms/posts?page=0&size=10&search=xyz&category=abc&status=PUBLISHED
     */
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getAllPosts(String search, String category, String statusStr, Pageable pageable) {
        PostStatus statusEnum = null;
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                statusEnum = PostStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid status for filtering
            }
        }
        return postRepository.searchAdminPosts(search, category, statusEnum, pageable).map(this::toResponseDto);
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPostById(UUID id) {
        return toResponseDto(findOrThrow(id));
    }

    @Transactional
    public PostResponseDto createPost(PostRequestDto dto) {
        if (dto.getSlug() != null && postRepository.existsBySlug(dto.getSlug())) {
            throw new DuplicateResourceException("Post", "slug", dto.getSlug());
        }

        UUID currentUserId = auditHelper.getCurrentUserId();
        OffsetDateTime now = OffsetDateTime.now();
        Post post = Post.builder()
                .title(dto.getTitle())
                .slug(dto.getSlug())
                .excerpt(dto.getExcerpt())
                .content(dto.getContent())
                .featuredImageUrl(dto.getFeaturedImageUrl())
                .sliderImageIds(dto.getSliderImageIds())
                .authorId(currentUserId)
                .category(resolveCategory(dto.getCategoryId()))
                .department(resolveDepartment(dto.getDepartmentId()))
                .tags(resolveTags(dto.getTagIds()))
                .metadata(dto.getMetadata())
                .status(dto.getStatus() != null ? dto.getStatus() : PostStatus.DRAFT)
                .publishedAt(dto.getStatus() == PostStatus.PUBLISHED ? now : null)
                .createdBy(currentUserId)
                .lastUpdatedBy(currentUserId)
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
        post.setSliderImageIds(dto.getSliderImageIds());
        post.setCategory(resolveCategory(dto.getCategoryId()));
        post.setDepartment(resolveDepartment(dto.getDepartmentId()));
        post.getTags().clear();
        post.getTags().addAll(resolveTags(dto.getTagIds()));
        if (dto.getMetadata() != null) post.setMetadata(dto.getMetadata());
        post.setLastUpdatedBy(auditHelper.getCurrentUserId());

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
     * If the post is already ARCHIVED, it permanently hard-deletes the record from the database.
     * @return true if permanently deleted, false if archived (soft-deleted)
     */
    @Transactional
    public boolean deleteOrArchivePost(UUID id) {
        Post post = findOrThrow(id);
        if (post.getStatus() == PostStatus.ARCHIVED) {
            postRepository.delete(post);
            return true;
        } else {
            post.setStatus(PostStatus.ARCHIVED);
            post.setLastUpdatedBy(auditHelper.getCurrentUserId());
            postRepository.save(post);
            return false;
        }
    }
}
