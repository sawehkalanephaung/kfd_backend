package com.kfd.api.kfd_backend.page;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "pages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(nullable = false, length = 512)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "hero_image_id")
    private UUID heroImageId;

    @Builder.Default
    @Column(length = 50)
    private String status = "DRAFT";

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "last_updated_by")
    private UUID lastUpdatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
