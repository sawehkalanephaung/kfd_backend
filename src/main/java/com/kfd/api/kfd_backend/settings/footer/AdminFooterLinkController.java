package com.kfd.api.kfd_backend.settings.footer;

import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/footer-links")
@RequiredArgsConstructor
public class AdminFooterLinkController {

    private final FooterLinkSectionService service;

    // ─── Section endpoints ────────────────────────────────────

    @GetMapping("/sections")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<FooterLinkSectionResponseDTO>> getAllSections() {
        return ResponseEntity.ok(service.getAllSections());
    }

    @PostMapping("/sections")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<FooterLinkSectionResponseDTO> createSection(
            @Valid @RequestBody FooterLinkSectionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createSection(dto));
    }

    @PutMapping("/sections/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<FooterLinkSectionResponseDTO> updateSection(
            @PathVariable UUID id,
            @Valid @RequestBody FooterLinkSectionRequestDTO dto) {
        return ResponseEntity.ok(service.updateSection(id, dto));
    }

    @DeleteMapping("/sections/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiMessageResponse> deleteSection(@PathVariable UUID id) {
        service.deleteSection(id);
        return ResponseEntity.ok(new ApiMessageResponse(200, "Footer link section deleted successfully."));
    }

    // ─── Link endpoints ───────────────────────────────────────

    @PostMapping("/sections/{sectionId}/links")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<FooterLinkResponseDTO> createLink(
            @PathVariable UUID sectionId,
            @Valid @RequestBody FooterLinkRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createLink(sectionId, dto));
    }

    @PutMapping("/links/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<FooterLinkResponseDTO> updateLink(
            @PathVariable UUID id,
            @Valid @RequestBody FooterLinkRequestDTO dto) {
        return ResponseEntity.ok(service.updateLink(id, dto));
    }

    @DeleteMapping("/links/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiMessageResponse> deleteLink(@PathVariable UUID id) {
        service.deleteLink(id);
        return ResponseEntity.ok(new ApiMessageResponse(200, "Footer link deleted successfully."));
    }
}
