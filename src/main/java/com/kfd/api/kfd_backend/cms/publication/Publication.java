package com.kfd.api.kfd_backend.cms.publication;

import com.kfd.api.kfd_backend.cms.publicationcategory.PublicationCategory;
import com.kfd.api.kfd_backend.department.Department;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "publications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private PublicationCategory category;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(name = "issued_by", length = 150)
    private String issuedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Builder.Default
    @Column(length = 50)
    private String language = "English";

    @Column(name = "reference_no", length = 100)
    private String referenceNo;

    // document_id / thumbnail_id — plain UUIDs resolved manually via MediaAssetRepository,
    // matching the pages.hero_image_id convention (real DB FK, no JPA association).
    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "thumbnail_id")
    private UUID thumbnailId;

    @Column(unique = true, length = 255)
    private String slug;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PublicationStatus status = PublicationStatus.DRAFT;

    @Column(name = "download_count")
    @Builder.Default
    private Integer downloadCount = 0;

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
