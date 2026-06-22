package com.kfd.api.kfd_backend.settings.footer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/footer-links")
@RequiredArgsConstructor
public class PublicFooterLinkController {

    private final FooterLinkSectionService service;

    @GetMapping
    public ResponseEntity<List<FooterLinkSectionResponseDTO>> getActiveFooterLinks() {
        return ResponseEntity.ok(service.getActiveSections());
    }
}
