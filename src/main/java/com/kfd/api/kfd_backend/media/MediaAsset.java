package com.kfd.api.kfd_backend.media;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "media_assets")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_url", length = 1024)
    private String fileUrl;

    @Column(name = "file_type", length = 100)
    private String fileType;

    @Column(name = "file_size_kb")
    private Integer fileSizeKb;

    @Column(name = "media_category", length = 255)
    private String mediaCategory;

    @Column(name = "language", length = 100)
    private String language;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "file_path", length = 1024)
    private String filePath;

    @CreatedBy
    @Column(name = "uploaded_by")
    private UUID uploadedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Collapses blank/whitespace-only category to null and trims real values,
     * so "uncategorized" has exactly one representation regardless of which
     * write path set it (upload leaves it null when omitted; the edit form
     * always sends the field, blank or not). Centralizing this here — instead
     * of each caller normalizing its own request param — means a future write
     * path can't reintroduce the same null-vs-"" split by forgetting to.
     */
    @PrePersist
    @PreUpdate
    private void normalizeMediaCategory() {
        if (mediaCategory != null) {
            String trimmed = mediaCategory.trim();
            mediaCategory = trimmed.isEmpty() ? null : trimmed;
        }
    }
}
