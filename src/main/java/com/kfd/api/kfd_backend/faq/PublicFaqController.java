package com.kfd.api.kfd_backend.faq;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public FAQ endpoint — returns only published FAQs, no auth required.
 * Secured at the path level by SecurityConfig: /api/v1/public/** is permitAll.
 */
@RestController
@RequestMapping("/api/v1/public/faqs")
@RequiredArgsConstructor
public class PublicFaqController {

    private final FaqService faqService;

    /** GET /api/v1/public/faqs — fetch only published FAQs */
    @GetMapping
    public ResponseEntity<ApiDataResponse<List<FaqDto>>> getPublishedFaqs() {
        return ResponseEntity.ok(
                new ApiDataResponse<>(200, "FAQs retrieved successfully", faqService.getPublicFaqs()));
    }
}
