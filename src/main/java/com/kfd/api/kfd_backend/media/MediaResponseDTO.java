package com.kfd.api.kfd_backend.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponseDTO {
    private UUID id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Integer fileSizeKb;
    private String mediaCategory;
    private UUID departmentId;
    private UUID uploadedBy;
    private OffsetDateTime createdAt;
}
