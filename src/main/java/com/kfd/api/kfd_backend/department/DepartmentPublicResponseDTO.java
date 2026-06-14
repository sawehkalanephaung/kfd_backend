package com.kfd.api.kfd_backend.department;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Public-facing Department response.
 * Embeds the head member, team members, and posts so the frontend
 * can render the Header, Staff, and Activity tabs in a single API call.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentPublicResponseDTO {
    private UUID id;
    private String slug;
    private String name;
    /** Full summary of the head member with photo, title, and bio link. */
    private TeamMemberSummaryDTO headMember;
    private String bodyContent;
    private UUID logoId;
    private UUID heroImageId;
    private String status;
    private Integer orderIndex;
    private List<DepartmentContactResponseDTO> contacts;
    private List<TeamMemberSummaryDTO> teamMembers;
    private List<PostSummaryDTO> posts;
}
