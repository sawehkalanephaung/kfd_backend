package com.kfd.api.kfd_backend.cms.tag;

import com.kfd.api.kfd_backend.cms.post.PostRepository;
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
public class TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    private TagDto toDto(Tag tag) {
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .createdAt(tag.getCreatedAt())
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

    private Tag findOrThrow(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
    }

    public TagDto getTagById(UUID id) {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public TagDto updateTag(UUID id, TagDto dto) {
        Tag tag = findOrThrow(id);

        if (!java.util.Objects.equals(tag.getSlug(), dto.getSlug()) && tagRepository.existsBySlug(dto.getSlug())) {
            throw new DuplicateResourceException("Tag", "slug", dto.getSlug());
        }

        tag.setName(dto.getName());
        tag.setSlug(dto.getSlug());
        return toDto(tagRepository.save(tag));
    }

    @Transactional
    public void deleteTag(UUID id) {
        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tag", "id", id);
        }
        if (postRepository.existsByTagId(id)) {
            throw new ResourceInUseException("Tag", "posts");
        }
        tagRepository.deleteById(id);
    }
}
