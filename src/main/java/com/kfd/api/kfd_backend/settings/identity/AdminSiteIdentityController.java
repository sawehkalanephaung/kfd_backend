package com.kfd.api.kfd_backend.settings.identity;

import com.kfd.api.kfd_backend.audit.AuditHelper;
import com.kfd.api.kfd_backend.audit.AuditLogService;
import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
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

/**
 * Admin management of the organization's identity.
 *
 * Guarded with the same authority as the other settings screens, so permissions are
 * governed entirely by the existing role/permission model — no separate mechanism.
 */
@RestController
@RequestMapping("/api/v1/admin/site-identity")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('manage_settings') or hasAuthority('ROLE_SUPER_ADMIN')")
public class AdminSiteIdentityController {

    private final SiteIdentityService service;
    private final AuditLogService auditLogService;
    private final AuditHelper auditHelper;

    /** The singleton record the admin screen edits. */
    @GetMapping
    public ResponseEntity<SiteIdentityResponseDTO> getSiteIdentity() {
        return ResponseEntity.ok(service.getSiteIdentity());
    }

    @GetMapping("/all")
    public ResponseEntity<List<SiteIdentityResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<ApiDataResponse<SiteIdentityResponseDTO>> create(
            @Valid @RequestBody SiteIdentityRequestDTO dto,
            HttpServletRequest request) {

        SiteIdentityResponseDTO created = service.create(dto);
        auditLogService.log(auditHelper.getCurrentUserId(), "CREATE", "SITE_IDENTITY", created.id(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiDataResponse<>(
                HttpStatus.CREATED.value(), "Organization identity created successfully", created));
    }

    /** Updates the singleton record, creating it if the table is empty. */
    @PutMapping
    public ResponseEntity<SiteIdentityResponseDTO> updateSiteIdentity(
            @Valid @RequestBody SiteIdentityRequestDTO dto,
            HttpServletRequest request) {

        SiteIdentityResponseDTO updated = service.updateSiteIdentity(dto);
        auditLogService.log(auditHelper.getCurrentUserId(), "UPDATE", "SITE_IDENTITY", updated.id(), request);

        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SiteIdentityResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody SiteIdentityRequestDTO dto,
            HttpServletRequest request) {

        SiteIdentityResponseDTO updated = service.update(id, dto);
        auditLogService.log(auditHelper.getCurrentUserId(), "UPDATE", "SITE_IDENTITY", id, request);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiMessageResponse> delete(
            @PathVariable UUID id,
            HttpServletRequest request) {

        service.delete(id);
        auditLogService.log(auditHelper.getCurrentUserId(), "DELETE", "SITE_IDENTITY", id, request);

        return ResponseEntity.ok(new ApiMessageResponse(
                HttpStatus.OK.value(), "Organization identity deleted successfully"));
    }
}
