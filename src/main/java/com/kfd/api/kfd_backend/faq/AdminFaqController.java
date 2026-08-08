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

/**
 * Admin FAQ endpoints — all routes require authentication.
 * Secured at path level by SecurityConfig: /api/v1/admin/** requires authenticated().
 * Fine-grained role checks are enforced via @PreAuthorize.
 */
@RestController
@RequestMapping("/api/v1/admin/faqs")
@RequiredArgsConstructor
public class AdminFaqController {

    private final FaqService faqService;
    private final AuditLogService auditLogService;
    private final AuditHelper auditHelper;

    /** GET /api/v1/admin/faqs — fetch all FAQs including drafts */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<ApiDataResponse<List<Faq>>> getAllFaqs() {
        return ResponseEntity.ok(
                new ApiDataResponse<>(200, "FAQs retrieved successfully", faqService.getAllFaqs()));
    }

    /** GET /api/v1/admin/faqs/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<ApiDataResponse<Faq>> getFaqById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                new ApiDataResponse<>(200, "FAQ retrieved successfully", faqService.getFaqById(id)));
    }

    /** POST /api/v1/admin/faqs */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<Faq>> createFaq(
            @RequestBody FaqDto dto,
            HttpServletRequest request) {
        UUID currentUserId = auditHelper.getCurrentUserId();
        Faq created = faqService.createFaq(dto, currentUserId);
        auditLogService.log(currentUserId, "CREATE", "FAQ", created.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiDataResponse<>(HttpStatus.CREATED.value(),
                        String.format("FAQ '%s' was successfully created.", created.getId()), created));
    }

    /** PUT /api/v1/admin/faqs/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<Faq>> updateFaq(
            @PathVariable UUID id,
            @RequestBody FaqDto dto,
            HttpServletRequest request) {
        UUID currentUserId = auditHelper.getCurrentUserId();
        Faq updated = faqService.updateFaq(id, dto, currentUserId);
        auditLogService.log(currentUserId, "UPDATE", "FAQ", id, request);
        return ResponseEntity.ok(
                new ApiDataResponse<>(HttpStatus.OK.value(),
                        String.format("FAQ '%s' was successfully updated.", updated.getId()), updated));
    }

    /** DELETE /api/v1/admin/faqs/{id} */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiMessageResponse> deleteFaq(
            @PathVariable UUID id,
            HttpServletRequest request) {
        faqService.deleteFaq(id);
        auditLogService.log(auditHelper.getCurrentUserId(), "DELETE", "FAQ", id, request);
        return ResponseEntity.ok(
                new ApiMessageResponse(HttpStatus.OK.value(),
                        String.format("FAQ '%s' was successfully deleted.", id)));
    }
}
