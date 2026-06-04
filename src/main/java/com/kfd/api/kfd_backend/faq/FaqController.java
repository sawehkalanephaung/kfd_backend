package com.kfd.api.kfd_backend.faq;

import lombok.RequiredArgsConstructor;
import java.util.List;
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
    public ResponseEntity<Faq> createFaq(@RequestBody FaqDto dto) {
        // Placeholder User ID until Authentication feature is implemented
        UUID mockAdminId = UUID.fromString("d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80");
        Faq created = faqService.createFaq(dto, mockAdminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Faq> updateFaq(@PathVariable UUID id, @RequestBody FaqDto dto) {
        UUID mockAdminId = UUID.fromString("d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80");
        Faq updated = faqService.updateFaq(id, dto, mockAdminId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable UUID id) {
        faqService.deleteFaq(id);
        return ResponseEntity.noContent().build();
    }
} // end of class
