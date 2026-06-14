package com.kfd.api.kfd_backend.settings;

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
@RequestMapping("/api/v1/admin/contact-settings")
@RequiredArgsConstructor
public class AdminContactSettingsController {

    private final ContactSettingsService contactSettingsService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactSettingsResponseDTO>> getAllSettings() {
        return ResponseEntity.ok(contactSettingsService.getAllSettings());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactSettingsResponseDTO> getSettingsById(@PathVariable UUID id) {
        return ResponseEntity.ok(contactSettingsService.getSettingsById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactSettingsResponseDTO> createSettings(@Valid @RequestBody ContactSettingsRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactSettingsService.createSettings(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactSettingsResponseDTO> updateSettings(
            @PathVariable UUID id,
            @Valid @RequestBody ContactSettingsRequestDTO dto) {
        return ResponseEntity.ok(contactSettingsService.updateSettings(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiMessageResponse> deleteSettings(@PathVariable UUID id) {
        contactSettingsService.deleteSettings(id);
        return ResponseEntity.ok(new ApiMessageResponse(200, "Contact settings deleted successfully."));
    }
}
