package com.kfd.api.kfd_backend.settings.newsletter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsletterSubscriptionService {

    private final NewsletterSubscriberRepository repository;

    /**
     * Public subscribe: idempotent — if the email already exists and is inactive,
     * reactivate it. If already active, just return success.
     */
    public NewsletterSubscriberResponseDTO subscribe(NewsletterSubscribeRequestDTO dto) {
        String normalizedEmail = dto.email().trim().toLowerCase();

        NewsletterSubscriber subscriber = repository.findByEmail(normalizedEmail)
                .map(existing -> {
                    if (!existing.getIsActive()) {
                        existing.setIsActive(true);
                        existing.setUnsubscribedAt(null);
                        existing.setSubscribedAt(LocalDateTime.now());
                    }
                    return repository.save(existing);
                })
                .orElseGet(() -> {
                    NewsletterSubscriber newSub = NewsletterSubscriber.builder()
                            .email(normalizedEmail)
                            .isActive(true)
                            .build();
                    return repository.save(newSub);
                });

        return NewsletterSubscriberResponseDTO.from(subscriber);
    }

    /**
     * Admin: list all subscribers.
     */
    public List<NewsletterSubscriberResponseDTO> getAllSubscribers() {
        return repository.findAll().stream()
                .map(NewsletterSubscriberResponseDTO::from)
                .toList();
    }

    /**
     * Admin: delete a subscriber.
     */
    public void deleteSubscriber(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Newsletter subscriber not found: " + id);
        }
        repository.deleteById(id);
    }
}
