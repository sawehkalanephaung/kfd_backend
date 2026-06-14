package com.kfd.api.kfd_backend.department;

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
public class DepartmentAdminResponseDTO {
    private UUID id;
    private String slug;
    private String name;
    /** Embedded summary of the head member — null if not yet assigned. */
    private TeamMemberSummaryDTO headMember;
    private String bodyContent;
    private UUID logoId;
    private UUID heroImageId;
    private String status;
    private Integer orderIndex;
    private UUID createdBy;
    private UUID lastUpdatedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
