package com.kfd.api.kfd_backend.settings.newsletter;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "newsletter_subscribers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsletterSubscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "subscribed_at", updatable = false)
    private LocalDateTime subscribedAt = LocalDateTime.now();

    @Column(name = "unsubscribed_at")
    private LocalDateTime unsubscribedAt;
}
