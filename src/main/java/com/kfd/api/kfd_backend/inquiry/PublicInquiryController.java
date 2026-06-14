package com.kfd.api.kfd_backend.inquiry;

import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/inquiries")
@RequiredArgsConstructor
public class PublicInquiryController {

    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<ApiMessageResponse> submitInquiry(@Valid @RequestBody InquiryRequestDTO dto) {
        mailService.sendInquiryEmail(dto);
        return ResponseEntity.ok(new ApiMessageResponse(200, "Your inquiry has been sent successfully."));
    }
}
