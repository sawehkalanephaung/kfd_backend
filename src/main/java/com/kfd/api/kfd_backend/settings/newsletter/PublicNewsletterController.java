package com.kfd.api.kfd_backend.settings.newsletter;

import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/newsletter")
@RequiredArgsConstructor
public class PublicNewsletterController {

    private final NewsletterSubscriptionService service;

    @PostMapping("/subscribe")
    public ResponseEntity<ApiMessageResponse> subscribe(
            @Valid @RequestBody NewsletterSubscribeRequestDTO dto) {
        service.subscribe(dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiMessageResponse(200, "Successfully subscribed to the newsletter."));
    }
}
