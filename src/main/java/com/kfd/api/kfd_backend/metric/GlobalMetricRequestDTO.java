package com.kfd.api.kfd_backend.metric;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalMetricRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Metric value is required")
    private String metricValue;

    private String icon;

    @Builder.Default
    private Integer displayOrder = 0;

    @Builder.Default
    private Boolean isActive = true;
}
