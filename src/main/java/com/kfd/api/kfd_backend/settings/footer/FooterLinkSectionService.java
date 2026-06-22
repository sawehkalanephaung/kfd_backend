package com.kfd.api.kfd_backend.settings.footer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FooterLinkSectionService {

    private final FooterLinkSectionRepository sectionRepository;
    private final FooterLinkRepository linkRepository;

    // ─── Section CRUD ─────────────────────────────────────────

    public List<FooterLinkSectionResponseDTO> getAllSections() {
        return sectionRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(FooterLinkSectionResponseDTO::from)
                .toList();
    }

    public List<FooterLinkSectionResponseDTO> getActiveSections() {
        return sectionRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(FooterLinkSectionResponseDTO::fromPublic)
                .toList();
    }

    public FooterLinkSectionResponseDTO createSection(FooterLinkSectionRequestDTO dto) {
        FooterLinkSection section = FooterLinkSection.builder()
                .title(dto.title())
                .displayOrder(dto.displayOrder())
                .isActive(dto.isActive() != null ? dto.isActive() : true)
                .build();
        return FooterLinkSectionResponseDTO.from(sectionRepository.save(section));
    }

    public FooterLinkSectionResponseDTO updateSection(UUID id, FooterLinkSectionRequestDTO dto) {
        FooterLinkSection section = sectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Footer link section not found: " + id));

        section.setTitle(dto.title());
        section.setDisplayOrder(dto.displayOrder());
        if (dto.isActive() != null) {
            section.setIsActive(dto.isActive());
        }
        return FooterLinkSectionResponseDTO.from(sectionRepository.save(section));
    }

    public void deleteSection(UUID id) {
        if (!sectionRepository.existsById(id)) {
            throw new RuntimeException("Footer link section not found: " + id);
        }
        sectionRepository.deleteById(id);
    }

    // ─── Link CRUD ────────────────────────────────────────────

    public FooterLinkResponseDTO createLink(UUID sectionId, FooterLinkRequestDTO dto) {
        FooterLinkSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Footer link section not found: " + sectionId));

        FooterLink link = FooterLink.builder()
                .section(section)
                .label(dto.label())
                .url(dto.url())
                .displayOrder(dto.displayOrder())
                .isActive(dto.isActive() != null ? dto.isActive() : true)
                .build();
        return FooterLinkResponseDTO.from(linkRepository.save(link));
    }

    public FooterLinkResponseDTO updateLink(UUID linkId, FooterLinkRequestDTO dto) {
        FooterLink link = linkRepository.findById(linkId)
                .orElseThrow(() -> new RuntimeException("Footer link not found: " + linkId));

        link.setLabel(dto.label());
        link.setUrl(dto.url());
        link.setDisplayOrder(dto.displayOrder());
        if (dto.isActive() != null) {
            link.setIsActive(dto.isActive());
        }
        return FooterLinkResponseDTO.from(linkRepository.save(link));
    }

    public void deleteLink(UUID linkId) {
        if (!linkRepository.existsById(linkId)) {
            throw new RuntimeException("Footer link not found: " + linkId);
        }
        linkRepository.deleteById(linkId);
    }
}
