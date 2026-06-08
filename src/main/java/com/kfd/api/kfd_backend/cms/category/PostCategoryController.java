package com.kfd.api.kfd_backend.cms.category;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/cms/categories")
@RequiredArgsConstructor
public class PostCategoryController {

    private final PostCategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<PostCategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<ApiDataResponse<PostCategoryDto>> createCategory(@RequestBody PostCategoryDto dto) {
        PostCategoryDto created = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiDataResponse<>(
                        HttpStatus.CREATED.value(),
                        String.format("Category '%s' was successfully created.", created.getName()),
                        created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiDataResponse<PostCategoryDto>> updateCategory(
            @PathVariable UUID id, @RequestBody PostCategoryDto dto) {
        PostCategoryDto updated = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(
                new ApiDataResponse<>(
                        HttpStatus.OK.value(),
                        String.format("Category '%s' was successfully updated.", updated.getName()),
                        updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiMessageResponse> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(
                new ApiMessageResponse(
                        HttpStatus.OK.value(),
                        String.format("Category with ID '%s' was successfully deleted.", id)));
    }
}
