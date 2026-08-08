package com.kfd.api.kfd_backend.cms.category;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/categories")
@RequiredArgsConstructor
public class PublicPostCategoryController {

    private final PostCategoryService categoryService;

    /**
     * GET /api/v1/public/categories
     * Returns only categories flagged as visible on the public news page filter bar.
     */
    @GetMapping
    public ResponseEntity<ApiDataResponse<List<PostCategoryDto>>> getAllCategories() {
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Categories retrieved successfully",
                categoryService.getPublicCategories()));
    }
}
