package com.kfd.api.kfd_backend.settings.newsletter;

import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/newsletter")
@RequiredArgsConstructor
public class AdminNewsletterController {

    private final NewsletterSubscriptionService service;

    @GetMapping("/subscribers")
    @PreAuthorize("hasAuthority('manage_content') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<NewsletterSubscriberResponseDTO>> getAllSubscribers() {
        return ResponseEntity.ok(service.getAllSubscribers());
    }

    @DeleteMapping("/subscribers/{id}")
    @PreAuthorize("hasAuthority('manage_content') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiMessageResponse> deleteSubscriber(@PathVariable UUID id) {
        service.deleteSubscriber(id);
        return ResponseEntity.ok(new ApiMessageResponse(200, "Newsletter subscriber deleted successfully."));
    }
}
