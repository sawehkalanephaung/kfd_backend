package com.kfd.api.kfd_backend.cms.post;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/cms/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * GET /api/v1/admin/cms/posts?page=0&size=10&search=xyz&category=abc
     * Returns all posts (DRAFT, PUBLISHED, ARCHIVED) paginated for the admin table.
     */
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(search, category, pageable));
    }

    /**
     * GET /api/v1/admin/cms/posts/{id}
     * Returns full post details by UUID — used to populate the edit form.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    /**
     * POST /api/v1/admin/cms/posts
     * Creates a new post. Defaults to DRAFT if no status is provided.
     */
    @PostMapping
    public ResponseEntity<ApiDataResponse<PostResponseDto>> createPost(@RequestBody PostRequestDto dto) {
        PostResponseDto created = postService.createPost(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiDataResponse<>(
                        HttpStatus.CREATED.value(),
                        String.format("Post '%s' was successfully created.", created.getTitle()),
                        created));
    }

    /**
     * PUT /api/v1/admin/cms/posts/{id}
     * Updates an existing post. Replaces the full tag set.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiDataResponse<PostResponseDto>> updatePost(
            @PathVariable UUID id, @RequestBody PostRequestDto dto) {
        PostResponseDto updated = postService.updatePost(id, dto);
        return ResponseEntity.ok(
                new ApiDataResponse<>(
                        HttpStatus.OK.value(),
                        String.format("Post '%s' was successfully updated.", updated.getTitle()),
                        updated));
    }

    /**
     * DELETE /api/v1/admin/cms/posts/{id}
     * Soft-deletes a post by setting status to ARCHIVED.
     * If the post is already ARCHIVED, permanently hard-deletes it.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiMessageResponse> deleteOrArchivePost(@PathVariable UUID id) {
        boolean isPermanentlyDeleted = postService.deleteOrArchivePost(id);
        String message = isPermanentlyDeleted
                ? String.format("Post with ID '%s' was permanently deleted.", id)
                : String.format("Post with ID '%s' was successfully archived.", id);
        return ResponseEntity.ok(new ApiMessageResponse(HttpStatus.OK.value(), message));
    }
}
