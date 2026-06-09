package com.kfd.api.kfd_backend.media;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "media_assets")
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

    @Column(name = "uploaded_by")
    private UUID uploadedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
