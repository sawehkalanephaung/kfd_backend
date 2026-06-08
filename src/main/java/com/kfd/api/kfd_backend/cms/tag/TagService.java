package com.kfd.api.kfd_backend.cms.tag;

import com.kfd.api.kfd_backend.global.exception.DuplicateResourceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    private TagDto toDto(Tag tag) {
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .build();
    }

    public List<TagDto> getAllTags() {
        return tagRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public TagDto createTag(TagDto dto) {
        if (tagRepository.existsBySlug(dto.getSlug())) {
            throw new DuplicateResourceException("Tag", "slug", dto.getSlug());
        }
        Tag saved = tagRepository.save(
                Tag.builder()
                        .name(dto.getName())
                        .slug(dto.getSlug())
                        .build()
        );
        return toDto(saved);
    }
}
