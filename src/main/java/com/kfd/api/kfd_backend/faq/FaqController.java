package com.kfd.api.kfd_backend.faq;

import lombok.RequiredArgsConstructor;
import java.util.List;
import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/faqs")
@RequiredArgsConstructor
public class FaqController {
    private final FaqService faqService;

    // public endpoint: Fetch publiched FAQs
    @GetMapping
    public ResponseEntity<List<FaqDto>> getPublishedFaqs() {
        return ResponseEntity.ok(faqService.getPublicFaqs());
    }

    // Admin endpoint: Fetch all FAQs (including drafts)
    @GetMapping("/admin")
    public ResponseEntity<List<Faq>> getAllFaqs() {
        return ResponseEntity.ok(faqService.getAllFaqs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faq> getFaqById(@PathVariable UUID id) {
        return ResponseEntity.ok(faqService.getFaqById(id));
    }

    @PostMapping
    public ResponseEntity<ApiDataResponse<Faq>> createFaq(@RequestBody FaqDto dto) {
        // Placeholder User ID until Authentication feature is implemented
        UUID mockAdminId = UUID.fromString("d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80");
        Faq created = faqService.createFaq(dto, mockAdminId);
        ApiDataResponse<Faq> response = new ApiDataResponse<>(
                HttpStatus.CREATED.value(),
                String.format("FAQ '%s' was successfully created.", created.getId()),
                created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiDataResponse<Faq>> updateFaq(@PathVariable UUID id, @RequestBody FaqDto dto) {
        UUID mockAdminId = UUID.fromString("d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80");
        Faq updated = faqService.updateFaq(id, dto, mockAdminId);
        ApiDataResponse<Faq> response = new ApiDataResponse<>(
                HttpStatus.OK.value(),
                String.format("FAQ '%s' was successfully updated.", updated.getId()),
                updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiMessageResponse> deleteFaq(@PathVariable UUID id) {
        faqService.deleteFaq(id);
        ApiMessageResponse response = new ApiMessageResponse(
                HttpStatus.OK.value(),
                String.format("FAQ '%s' was successfully deleted.", id));
        return ResponseEntity.ok(response);
    }
} // end of class
