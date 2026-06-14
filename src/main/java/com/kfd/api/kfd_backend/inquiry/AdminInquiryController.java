package com.kfd.api.kfd_backend.inquiry;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import org.springframework.http.HttpStatus;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/inquiries")
@RequiredArgsConstructor
public class AdminInquiryController {

    private final InquiryService inquiryService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Page<InquiryResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(inquiryService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<InquiryResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Inquiry retrieved", inquiryService.getById(id)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<InquiryResponseDTO>> updateStatus(@PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Status updated", inquiryService.updateStatus(id, status)));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiMessageResponse> delete(@PathVariable UUID id) {
        inquiryService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiMessageResponse(204, "Inquiry deleted"));
    }
}
