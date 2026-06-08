package com.kfd.api.kfd_backend.cms.category;

import com.kfd.api.kfd_backend.global.exception.DuplicateResourceException;
import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostCategoryService {

    private final PostCategoryRepository categoryRepository;

    // ── Helpers ──────────────────────────────────────────────

    private PostCategoryDto toDto(PostCategory category) {
        return PostCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .build();
    }

    private PostCategory findOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    // ── Public API ────────────────────────────────────────────

    public List<PostCategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public PostCategoryDto createCategory(PostCategoryDto dto) {
        if (categoryRepository.existsBySlug(dto.getSlug())) {
            throw new DuplicateResourceException("Category", "slug", dto.getSlug());
        }
        PostCategory saved = categoryRepository.save(
                PostCategory.builder()
                        .name(dto.getName())
                        .slug(dto.getSlug())
                        .description(dto.getDescription())
                        .build()
        );
        return toDto(saved);
    }

    @Transactional
    public PostCategoryDto updateCategory(UUID id, PostCategoryDto dto) {
        PostCategory category = findOrThrow(id);

        // Allow slug update only if not taken by another record
        if (!category.getSlug().equals(dto.getSlug()) && categoryRepository.existsBySlug(dto.getSlug())) {
            throw new DuplicateResourceException("Category", "slug", dto.getSlug());
        }

        category.setName(dto.getName());
        category.setSlug(dto.getSlug());
        category.setDescription(dto.getDescription());
        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(UUID id) {
        // Throws 404 if not found; FK violation (posts reference it) bubbles up
        // as DataIntegrityViolationException → caught by GlobalExceptionHandler → 409
        PostCategory category = findOrThrow(id);
        categoryRepository.delete(category);
    }
}
