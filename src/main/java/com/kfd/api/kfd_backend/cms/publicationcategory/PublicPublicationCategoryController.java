package com.kfd.api.kfd_backend.cms.publicationcategory;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/publication-categories")
@RequiredArgsConstructor
public class PublicPublicationCategoryController {

    private final PublicationCategoryService categoryService;

    /**
     * GET /api/v1/public/publication-categories
     * Returns only categories flagged as visible on the public publications filter bar.
     */
    @GetMapping
    public ResponseEntity<ApiDataResponse<List<PublicationCategoryDto>>> getAllCategories() {
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Categories retrieved successfully",
                categoryService.getPublicCategories()));
    }
}
