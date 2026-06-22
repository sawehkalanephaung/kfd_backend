package com.kfd.api.kfd_backend.settings.social;

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
@RequestMapping("/api/v1/admin/social-media")
@RequiredArgsConstructor
public class AdminSocialMediaController {

    private final SocialMediaLinkService service;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SocialMediaLinkResponseDTO>> getAllLinks() {
        return ResponseEntity.ok(service.getAllLinks());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SocialMediaLinkResponseDTO> createLink(@Valid @RequestBody SocialMediaLinkRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createLink(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<SocialMediaLinkResponseDTO> updateLink(
            @PathVariable UUID id,
            @Valid @RequestBody SocialMediaLinkRequestDTO dto) {
        return ResponseEntity.ok(service.updateLink(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiMessageResponse> deleteLink(@PathVariable UUID id) {
        service.deleteLink(id);
        return ResponseEntity.ok(new ApiMessageResponse(200, "Social media link deleted successfully."));
    }
}
