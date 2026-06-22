package com.kfd.api.kfd_backend.settings.identity;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/site-identity")
@RequiredArgsConstructor
public class PublicSiteIdentityController {

    private final SiteIdentityService siteIdentityService;

    @GetMapping
    public ResponseEntity<SiteIdentityResponseDTO> getSiteIdentity() {
        return ResponseEntity.ok(siteIdentityService.getSiteIdentity());
    }
}
