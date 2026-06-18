package com.kfd.api.kfd_backend.metric;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalMetricResponseDTO {
    private UUID id;
    private String title;
    private String metricValue;
    private String icon;
    private Integer displayOrder;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
