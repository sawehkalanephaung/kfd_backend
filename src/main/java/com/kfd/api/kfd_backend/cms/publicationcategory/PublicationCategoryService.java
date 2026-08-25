package com.kfd.api.kfd_backend.cms.publicationcategory;

import com.kfd.api.kfd_backend.cms.publication.PublicationRepository;
import com.kfd.api.kfd_backend.global.exception.DuplicateResourceException;
import com.kfd.api.kfd_backend.global.exception.ResourceInUseException;
import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicationCategoryService {

    private final PublicationCategoryRepository categoryRepository;
    private final PublicationRepository publicationRepository;

    // ── Helpers ──────────────────────────────────────────────

    private PublicationCategoryDto toDto(PublicationCategory category) {
        return PublicationCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .showInPublic(category.isShowInPublic())
                .createdAt(category.getCreatedAt())
                .build();
    }

    private PublicationCategory findOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PublicationCategory", "id", id));
    }

    // ── Public API ────────────────────────────────────────────

    public List<PublicationCategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /** Returns only categories that are flagged as visible on the public site. */
    public List<PublicationCategoryDto> getPublicCategories() {
        return categoryRepository.findAllByShowInPublicTrue()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public PublicationCategoryDto getCategoryById(UUID id) {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public PublicationCategoryDto createCategory(PublicationCategoryDto dto) {
        if (categoryRepository.existsBySlug(dto.getSlug())) {
            throw new DuplicateResourceException("PublicationCategory", "slug", dto.getSlug());
        }
        PublicationCategory saved = categoryRepository.save(
                PublicationCategory.builder()
                        .name(dto.getName())
                        .slug(dto.getSlug())
                        .description(dto.getDescription())
                        .showInPublic(dto.isShowInPublic())
                        .build()
        );
        return toDto(saved);
    }

    @Transactional
    public PublicationCategoryDto updateCategory(UUID id, PublicationCategoryDto dto) {
        PublicationCategory category = findOrThrow(id);

        if (!java.util.Objects.equals(category.getSlug(), dto.getSlug()) && categoryRepository.existsBySlug(dto.getSlug())) {
            throw new DuplicateResourceException("PublicationCategory", "slug", dto.getSlug());
        }

        category.setName(dto.getName());
        category.setSlug(dto.getSlug());
        category.setDescription(dto.getDescription());
        category.setShowInPublic(dto.isShowInPublic());
        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("PublicationCategory", "id", id);
        }
        if (publicationRepository.existsByCategoryId(id)) {
            throw new ResourceInUseException("PublicationCategory", "publications");
        }
        categoryRepository.deleteById(id);
    }
}
