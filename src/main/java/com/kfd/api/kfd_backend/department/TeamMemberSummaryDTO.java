package com.kfd.api.kfd_backend.department;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Lightweight team member summary embedded in DepartmentPublicResponseDTO. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberSummaryDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String title;
    private String headshotUrl;
    private Integer displayOrder;
    private Boolean isActive;
}
