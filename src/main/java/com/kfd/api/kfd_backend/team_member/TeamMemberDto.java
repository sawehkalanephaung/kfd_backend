package com.kfd.api.kfd_backend.team_member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String title;
    private UUID departmentId;
    private String departmentName;
    private String bio;
    private String headshotUrl;
    private Integer displayOrder;
    private Boolean isActive;
    
    @com.fasterxml.jackson.annotation.JsonProperty("isKfdChairman")
    private Boolean isKfdChairman;
}
