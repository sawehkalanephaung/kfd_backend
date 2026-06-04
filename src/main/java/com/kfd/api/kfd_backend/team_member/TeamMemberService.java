package com.kfd.api.kfd_backend.team_member;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    // Public: only active members
    public List<TeamMemberDto> getActiveMembers() {
        return teamMemberRepository.findByIsActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Admin: all members (active and inactive)
    public List<TeamMember> getAllMembers() {
        return teamMemberRepository.findAll();
    }

    public TeamMember getMemberById(UUID id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team member not found with ID: " + id));
    }

    @Transactional
    public TeamMember createMember(TeamMemberDto dto, UUID currentUserId) {
        TeamMember member = TeamMember.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .title(dto.getTitle())
                .department(dto.getDepartment())
                .bio(dto.getBio())
                .headshotUrl(dto.getHeadshotUrl())
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .createdBy(currentUserId)
                .lastUpdatedBy(currentUserId)
                .build();
        return teamMemberRepository.save(member);
    }

    @Transactional
    public TeamMember updateMember(UUID id, TeamMemberDto dto, UUID currentUserId) {
        TeamMember member = getMemberById(id);
        member.setFirstName(dto.getFirstName());
        member.setLastName(dto.getLastName());
        member.setTitle(dto.getTitle());
        member.setDepartment(dto.getDepartment());
        member.setBio(dto.getBio());
        member.setHeadshotUrl(dto.getHeadshotUrl());
        member.setDisplayOrder(dto.getDisplayOrder());
        member.setIsActive(dto.getIsActive());
        member.setLastUpdatedBy(currentUserId);
        return teamMemberRepository.save(member);
    }

    @Transactional
    public void deleteMember(UUID id) {
        TeamMember member = getMemberById(id);
        teamMemberRepository.delete(member);
    }

    // Helper: map Entity → DTO
    private TeamMemberDto toDto(TeamMember member) {
        return TeamMemberDto.builder()
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .title(member.getTitle())
                .department(member.getDepartment())
                .bio(member.getBio())
                .headshotUrl(member.getHeadshotUrl())
                .displayOrder(member.getDisplayOrder())
                .isActive(member.getIsActive())
                .build();
    }

} // end of class
