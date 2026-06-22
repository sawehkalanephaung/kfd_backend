package com.kfd.api.kfd_backend.department;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentTimelineEventDTO {
    private UUID id;
    private String year;
    private String title;
    private String description;
    private Integer orderIndex;

    public static DepartmentTimelineEventDTO from(DepartmentTimelineEvent event) {
        return DepartmentTimelineEventDTO.builder()
                .id(event.getId())
                .year(event.getYear())
                .title(event.getTitle())
                .description(event.getDescription())
                .orderIndex(event.getOrderIndex())
                .build();
    }
}
