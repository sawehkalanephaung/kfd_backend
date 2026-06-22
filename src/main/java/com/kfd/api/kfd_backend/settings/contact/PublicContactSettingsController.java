package com.kfd.api.kfd_backend.settings.contact;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/contact-settings")
@RequiredArgsConstructor
public class PublicContactSettingsController {

    private final ContactSettingsService contactSettingsService;

    @GetMapping
    public ResponseEntity<ContactSettingsResponseDTO> getDefaultSettings() {
        return ResponseEntity.ok(contactSettingsService.getDefaultSettings());
    }
}
