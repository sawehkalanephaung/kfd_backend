package com.kfd.api.kfd_backend.settings.identity;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-only branding for the public website.
 *
 * Sits under /api/v1/public/**, which SecurityConfig permits anonymously. There is
 * deliberately no write path here — modification lives only on the admin controller.
 */
@RestController
@RequestMapping("/api/v1/public/site-identity")
@RequiredArgsConstructor
public class PublicSiteIdentityController {

    private final SiteIdentityService service;

    @GetMapping
    public ResponseEntity<SiteIdentityResponseDTO> getSiteIdentity() {
        return ResponseEntity.ok(service.getSiteIdentity());
    }
}
