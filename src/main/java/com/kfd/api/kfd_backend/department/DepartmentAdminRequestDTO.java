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
public class DepartmentAdminRequestDTO {
    private String slug;
    private String name;
    /** UUID of the TeamMember who is the head of this department. Nullable. */
    private UUID headMemberId;
    private String bodyContent;
    private UUID logoId;
    private UUID heroImageId;
    private String status;
    private Integer orderIndex;
}
