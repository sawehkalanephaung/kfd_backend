package com.kfd.api.kfd_backend.inquiry;

import com.kfd.api.kfd_backend.audit.AuditLogService;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import com.kfd.api.kfd_backend.global.mail.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/public/inquiries")
@RequiredArgsConstructor
public class PublicInquiryController {

    private final MailService mailService;
    private final AuditLogService auditLogService;

    /**
     * Public endpoint — no authentication required.
     * Accepts an inquiry form submission, sends a notification email,
     * and logs the event to the audit log file (SLF4J only — no DB row since user is anonymous).
     */
    @PostMapping("/send")
    public ResponseEntity<ApiMessageResponse> submitInquiry(@Valid @RequestBody InquiryRequestDTO dto) {
        mailService.sendInquiryEmail(dto);

        // Log the submission to the audit file (SLF4J only — anonymous, no DB record)
        auditLogService.logAnonymous(
                "SUBMIT",
                "INQUIRY",
                String.format("sender=%s type=%s subject=%s", dto.senderEmail(), dto.inquiryType(), dto.subject())
        );

        log.info("Inquiry submitted: sender={}, type={}, subject={}", dto.senderEmail(), dto.inquiryType(), dto.subject());

        return ResponseEntity.ok(new ApiMessageResponse(200, "Your inquiry has been sent successfully."));
    }
}
