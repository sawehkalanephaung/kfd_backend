package com.kfd.api.kfd_backend.settings.social;

import com.kfd.api.kfd_backend.audit.AuditHelper;
import com.kfd.api.kfd_backend.audit.AuditLogService;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/social-media")
@RequiredArgsConstructor
public class AdminSocialMediaController {

    private final SocialMediaLinkService service;
    private final AuditLogService auditLogService;
    private final AuditHelper auditHelper;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SocialMediaLinkResponseDTO>> getAllLinks() {
        return ResponseEntity.ok(service.getAllLinks());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SocialMediaLinkResponseDTO> createLink(
            @Valid @RequestBody SocialMediaLinkRequestDTO dto,
            HttpServletRequest request) {
        SocialMediaLinkResponseDTO created = service.createLink(dto);
        auditLogService.log(auditHelper.getCurrentUserId(), "CREATE", "SOCIAL_MEDIA", created.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SocialMediaLinkResponseDTO> updateLink(
            @PathVariable UUID id,
            @Valid @RequestBody SocialMediaLinkRequestDTO dto,
            HttpServletRequest request) {
        SocialMediaLinkResponseDTO updated = service.updateLink(id, dto);
        auditLogService.log(auditHelper.getCurrentUserId(), "UPDATE", "SOCIAL_MEDIA", id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiMessageResponse> deleteLink(
            @PathVariable UUID id,
            HttpServletRequest request) {
        service.deleteLink(id);
        auditLogService.log(auditHelper.getCurrentUserId(), "DELETE", "SOCIAL_MEDIA", id, request);
        return ResponseEntity.ok(new ApiMessageResponse(200, "Social media link deleted successfully."));
    }
}
