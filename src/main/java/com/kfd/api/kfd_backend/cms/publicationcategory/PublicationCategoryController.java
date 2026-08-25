package com.kfd.api.kfd_backend.cms.publicationcategory;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/admin/cms/publication-categories")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('manage_content') or hasAuthority('ROLE_SUPER_ADMIN')")
public class PublicationCategoryController {

    private final PublicationCategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<PublicationCategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiDataResponse<PublicationCategoryDto>> getCategoryById(@PathVariable UUID id) {
        PublicationCategoryDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(
                new ApiDataResponse<>(
                        HttpStatus.OK.value(),
                        "Category retrieved successfully",
                        category));
    }

    @PostMapping
    public ResponseEntity<ApiDataResponse<PublicationCategoryDto>> createCategory(@RequestBody PublicationCategoryDto dto) {
        PublicationCategoryDto created = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiDataResponse<>(
                        HttpStatus.CREATED.value(),
                        String.format("Category '%s' was successfully created.", created.getName()),
                        created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiDataResponse<PublicationCategoryDto>> updateCategory(
            @PathVariable UUID id, @RequestBody PublicationCategoryDto dto) {
        PublicationCategoryDto updated = categoryService.updateCategory(id, dto);
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
