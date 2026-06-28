package com.kfd.api.kfd_backend.faq;

import com.kfd.api.kfd_backend.audit.AuditHelper;
import com.kfd.api.kfd_backend.audit.AuditLogService;
import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/faqs")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;
    private final AuditLogService auditLogService;
    private final AuditHelper auditHelper;

    // ─── Public Endpoints ─────────────────────────────────────────────────────

    /** Public endpoint: fetch only published FAQs */
    @GetMapping
    public ResponseEntity<List<FaqDto>> getPublishedFaqs() {
        return ResponseEntity.ok(faqService.getPublicFaqs());
    }

    // ─── Admin Endpoints ──────────────────────────────────────────────────────

    /** Admin endpoint: fetch all FAQs (including drafts) */
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<List<Faq>> getAllFaqs() {
        return ResponseEntity.ok(faqService.getAllFaqs());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<Faq> getFaqById(@PathVariable UUID id) {
        return ResponseEntity.ok(faqService.getFaqById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<Faq>> createFaq(
            @RequestBody FaqDto dto,
            HttpServletRequest request) {
        UUID currentUserId = auditHelper.getCurrentUserId();
        Faq created = faqService.createFaq(dto, currentUserId);
        auditLogService.log(currentUserId, "CREATE", "FAQ", created.getId(), request);
        ApiDataResponse<Faq> response = new ApiDataResponse<>(
                HttpStatus.CREATED.value(),
                String.format("FAQ '%s' was successfully created.", created.getId()),
                created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<Faq>> updateFaq(
            @PathVariable UUID id,
            @RequestBody FaqDto dto,
            HttpServletRequest request) {
        UUID currentUserId = auditHelper.getCurrentUserId();
        Faq updated = faqService.updateFaq(id, dto, currentUserId);
        auditLogService.log(currentUserId, "UPDATE", "FAQ", id, request);
        ApiDataResponse<Faq> response = new ApiDataResponse<>(
                HttpStatus.OK.value(),
                String.format("FAQ '%s' was successfully updated.", updated.getId()),
                updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiMessageResponse> deleteFaq(
            @PathVariable UUID id,
            HttpServletRequest request) {
        faqService.deleteFaq(id);
        auditLogService.log(auditHelper.getCurrentUserId(), "DELETE", "FAQ", id, request);
        ApiMessageResponse response = new ApiMessageResponse(
                HttpStatus.OK.value(),
                String.format("FAQ '%s' was successfully deleted.", id));
        return ResponseEntity.ok(response);
    }
}
