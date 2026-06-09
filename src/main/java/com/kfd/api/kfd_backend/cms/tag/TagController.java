package com.kfd.api.kfd_backend.cms.tag;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/cms/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @PostMapping
    public ResponseEntity<ApiDataResponse<TagDto>> createTag(@RequestBody TagDto dto) {
        TagDto created = tagService.createTag(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiDataResponse<>(
                        HttpStatus.CREATED.value(),
                        String.format("Tag '%s' was successfully created.", created.getName()),
                        created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiMessageResponse> deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok(
                new ApiMessageResponse(
                        HttpStatus.OK.value(),
                        String.format("Tag with ID '%s' was successfully deleted.", id)));
    }
}
