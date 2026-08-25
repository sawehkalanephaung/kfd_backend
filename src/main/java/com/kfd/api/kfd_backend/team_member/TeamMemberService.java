package com.kfd.api.kfd_backend.team_member;

import com.kfd.api.kfd_backend.department.Department;
import com.kfd.api.kfd_backend.department.DepartmentRepository;
import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final DepartmentRepository departmentRepository;

    public List<TeamMemberDto> getActiveMembers() {
        return teamMemberRepository.findByIsActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<TeamMemberDto> getAllMembers() {
        return teamMemberRepository.findAll().stream().map(this::toDto).toList();
    }

    public TeamMemberDto getMemberById(UUID id) {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public TeamMemberDto createMember(TeamMemberDto dto, UUID currentUserId) {
        Department department = resolveDepart(dto.getDepartmentId());
        TeamMember member = TeamMember.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .title(dto.getTitle())
                .department(department)
                .bio(dto.getBio())
                .headshotUrl(dto.getHeadshotUrl())
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .isKfdChairman(dto.getIsKfdChairman() != null ? dto.getIsKfdChairman() : false)
                .termStartDate(dto.getTermStartDate())
                .termEndDate(dto.getTermEndDate())
                .createdBy(currentUserId)
                .lastUpdatedBy(currentUserId)
                .build();
        return toDto(teamMemberRepository.save(member));
    }

    @Transactional
    public TeamMemberDto updateMember(UUID id, TeamMemberDto dto, UUID currentUserId) {
        TeamMember member = findOrThrow(id);
        member.setFirstName(dto.getFirstName());
        member.setLastName(dto.getLastName());
        member.setTitle(dto.getTitle());
        member.setDepartment(resolveDepart(dto.getDepartmentId()));
        member.setBio(dto.getBio());
        member.setHeadshotUrl(dto.getHeadshotUrl());
        member.setDisplayOrder(dto.getDisplayOrder());
        member.setIsActive(dto.getIsActive());
        member.setIsKfdChairman(dto.getIsKfdChairman());
        member.setTermStartDate(dto.getTermStartDate());
        member.setTermEndDate(dto.getTermEndDate());
        member.setLastUpdatedBy(currentUserId);
        return toDto(teamMemberRepository.save(member));
    }

    @Transactional
    public void deleteMember(UUID id) {
        teamMemberRepository.delete(findOrThrow(id));
    }

    private TeamMemberDto toDto(TeamMember m) {
        return TeamMemberDto.builder()
                .id(m.getId())
                .firstName(m.getFirstName())
                .lastName(m.getLastName())
                .title(m.getTitle())
                .departmentId(m.getDepartment() != null ? m.getDepartment().getId() : null)
                .departmentName(m.getDepartment() != null ? m.getDepartment().getName() : null)
                .bio(m.getBio())
                .headshotUrl(m.getHeadshotUrl())
                .displayOrder(m.getDisplayOrder())
                .isActive(m.getIsActive())
                .isKfdChairman(m.getIsKfdChairman())
                .termStartDate(m.getTermStartDate())
                .termEndDate(m.getTermEndDate())
                .build();
    }

    private TeamMember findOrThrow(UUID id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TeamMember", "id", id));
    }

    private Department resolveDepart(UUID departmentId) {
        if (departmentId == null) return null;
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", departmentId));
    }
}
