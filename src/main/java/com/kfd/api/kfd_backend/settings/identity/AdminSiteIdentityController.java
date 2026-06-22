package com.kfd.api.kfd_backend.settings.identity;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/site-identity")
@RequiredArgsConstructor
public class AdminSiteIdentityController {

    private final SiteIdentityService siteIdentityService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SiteIdentityResponseDTO> getSiteIdentity() {
        return ResponseEntity.ok(siteIdentityService.getSiteIdentity());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SiteIdentityResponseDTO> updateSiteIdentity(
            @RequestBody SiteIdentityRequestDTO dto) {
        return ResponseEntity.ok(siteIdentityService.updateSiteIdentity(dto));
    }
}
