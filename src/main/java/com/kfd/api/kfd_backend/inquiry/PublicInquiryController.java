package com.kfd.api.kfd_backend.inquiry;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/inquiries")
@RequiredArgsConstructor
public class PublicInquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public ResponseEntity<ApiDataResponse<InquiryResponseDTO>> submit(@RequestBody InquiryRequestDTO dto) {
        InquiryResponseDTO created = inquiryService.submit(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiDataResponse<>(201, "Inquiry submitted successfully", created));
    }
}
