package com.kfd.api.kfd_backend.page;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/pages")
@RequiredArgsConstructor
public class PublicPageController {

    private final PageService pageService;

    @GetMapping("/{slug}")
    public ResponseEntity<ApiDataResponse<PageResponseDTO>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Page retrieved", pageService.getBySlug(slug)));
    }
}
