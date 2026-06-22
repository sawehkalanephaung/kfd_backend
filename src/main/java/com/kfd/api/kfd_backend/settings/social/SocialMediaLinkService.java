package com.kfd.api.kfd_backend.settings.social;

import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialMediaLinkService {

    private final SocialMediaLinkRepository repository;

    @Transactional(readOnly = true)
    public List<SocialMediaLinkResponseDTO> getAllLinks() {
        return repository.findAllByOrderByDisplayOrderAsc().stream()
                .map(SocialMediaLinkResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SocialMediaLinkResponseDTO> getActiveLinks() {
        return repository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(SocialMediaLinkResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public SocialMediaLinkResponseDTO createLink(SocialMediaLinkRequestDTO dto) {
        SocialMediaLink link = SocialMediaLink.builder()
                .platformName(dto.platformName())
                .url(dto.url())
                .displayOrder(dto.displayOrder())
                .isActive(dto.isActive())
                .build();
        return SocialMediaLinkResponseDTO.fromEntity(repository.save(link));
    }

    @Transactional
    public SocialMediaLinkResponseDTO updateLink(UUID id, SocialMediaLinkRequestDTO dto) {
        SocialMediaLink link = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SocialMediaLink", "id", id.toString()));
        
        link.setPlatformName(dto.platformName());
        link.setUrl(dto.url());
        link.setDisplayOrder(dto.displayOrder());
        link.setIsActive(dto.isActive());
        
        return SocialMediaLinkResponseDTO.fromEntity(repository.save(link));
    }

    @Transactional
    public void deleteLink(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("SocialMediaLink", "id", id.toString());
        }
        repository.deleteById(id);
    }
}
