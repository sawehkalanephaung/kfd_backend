package com.kfd.api.kfd_backend.page;

import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

import com.kfd.api.kfd_backend.media.MediaAssetRepository;
import com.kfd.api.kfd_backend.media.MediaAsset;

@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final MediaAssetRepository mediaAssetRepository;

    private PageResponseDTO toDto(com.kfd.api.kfd_backend.page.Page p) {
        String heroImageUrl = null;
        if (p.getHeroImageId() != null) {
            heroImageUrl = mediaAssetRepository.findById(p.getHeroImageId())
                    .map(MediaAsset::getFileUrl)
                    .orElse(null);
        }

        List<String> sliderImageUrls = new ArrayList<>();
        if (p.getSliderImageIds() != null && !p.getSliderImageIds().isEmpty()) {
            sliderImageUrls = mediaAssetRepository.findAllById(p.getSliderImageIds())
                    .stream()
                    .map(MediaAsset::getFileUrl)
                    .collect(Collectors.toList());
        }

        return PageResponseDTO.builder()
                .id(p.getId())
                .slug(p.getSlug())
                .title(p.getTitle())
                .content(p.getContent())
                .heroImageId(p.getHeroImageId())
                .heroImageUrl(heroImageUrl)
                .sliderImageIds(p.getSliderImageIds())
                .sliderImageUrls(sliderImageUrls)
                .status(p.getStatus())
                .createdBy(p.getCreatedBy())
                .lastUpdatedBy(p.getLastUpdatedBy())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public Page<PageResponseDTO> getAll(Pageable pageable) {
        return pageRepository.findAll(pageable).map(this::toDto);
    }

    public PageResponseDTO getById(UUID id) {
        return toDto(findOrThrow(id));
    }

    public PageResponseDTO getBySlug(String slug) {
        return toDto(pageRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Page", "slug", slug)));
    }

    @Transactional
    public PageResponseDTO create(PageRequestDTO dto, UUID currentUserId) {
        com.kfd.api.kfd_backend.page.Page page = com.kfd.api.kfd_backend.page.Page.builder()
                .slug(dto.getSlug())
                .title(dto.getTitle())
                .content(dto.getContent())
                .heroImageId(dto.getHeroImageId())
                .sliderImageIds(dto.getSliderImageIds())
                .status(dto.getStatus() != null ? dto.getStatus() : "DRAFT")
                .createdBy(currentUserId)
                .lastUpdatedBy(currentUserId)
                .build();
        return toDto(pageRepository.save(page));
    }

    @Transactional
    public PageResponseDTO update(UUID id, PageRequestDTO dto, UUID currentUserId) {
        com.kfd.api.kfd_backend.page.Page page = findOrThrow(id);
        page.setSlug(dto.getSlug());
        page.setTitle(dto.getTitle());
        page.setContent(dto.getContent());
        page.setHeroImageId(dto.getHeroImageId());
        page.setSliderImageIds(dto.getSliderImageIds());
        if (dto.getStatus() != null) page.setStatus(dto.getStatus());
        page.setLastUpdatedBy(currentUserId);
        return toDto(pageRepository.save(page));
    }

    @Transactional
    public void delete(UUID id) {
        pageRepository.delete(findOrThrow(id));
    }

    private com.kfd.api.kfd_backend.page.Page findOrThrow(UUID id) {
        return pageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Page", "id", id));
    }
}
