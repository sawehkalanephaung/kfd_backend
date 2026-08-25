package com.kfd.api.kfd_backend.settings.contact;

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
@RequestMapping("/api/v1/admin/contact-settings")
@RequiredArgsConstructor
public class AdminContactSettingsController {

    private final ContactSettingsService contactSettingsService;
    private final AuditLogService auditLogService;
    private final AuditHelper auditHelper;

    @GetMapping
    @PreAuthorize("hasAuthority('manage_settings') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<ContactSettingsResponseDTO>> getAllSettings() {
        return ResponseEntity.ok(contactSettingsService.getAllSettings());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('manage_settings') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ContactSettingsResponseDTO> getSettingsById(@PathVariable UUID id) {
        return ResponseEntity.ok(contactSettingsService.getSettingsById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('manage_settings') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ContactSettingsResponseDTO> createSettings(
            @Valid @RequestBody ContactSettingsRequestDTO dto,
            HttpServletRequest request) {
        ContactSettingsResponseDTO created = contactSettingsService.createSettings(dto);
        auditLogService.log(auditHelper.getCurrentUserId(), "CREATE", "CONTACT_SETTINGS", created.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('manage_settings') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ContactSettingsResponseDTO> updateSettings(
            @PathVariable UUID id,
            @Valid @RequestBody ContactSettingsRequestDTO dto,
            HttpServletRequest request) {
        ContactSettingsResponseDTO updated = contactSettingsService.updateSettings(id, dto);
        auditLogService.log(auditHelper.getCurrentUserId(), "UPDATE", "CONTACT_SETTINGS", id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('manage_settings') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiMessageResponse> deleteSettings(
            @PathVariable UUID id,
            HttpServletRequest request) {
        contactSettingsService.deleteSettings(id);
        auditLogService.log(auditHelper.getCurrentUserId(), "DELETE", "CONTACT_SETTINGS", id, request);
        return ResponseEntity.ok(new ApiMessageResponse(200, "Contact settings deleted successfully."));
    }
}
