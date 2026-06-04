package com.kfd.api.kfd_backend.team_member;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String title;       // JSON string: {"en": "Executive Director"} from now
    private String department;
    private String bio;         // JSON string: {"en": "Bio text here..."} from now
    private String headshotUrl;
    private Integer displayOrder;
    private Boolean isActive;


}
