package com.kfd.api.kfd_backend.settings.social;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/social-media")
@RequiredArgsConstructor
public class PublicSocialMediaController {

    private final SocialMediaLinkService service;

    @GetMapping
    public ResponseEntity<List<SocialMediaLinkResponseDTO>> getActiveLinks() {
        return ResponseEntity.ok(service.getActiveLinks());
    }
}
