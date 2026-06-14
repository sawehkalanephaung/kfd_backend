package com.kfd.api.kfd_backend.team_member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
    List<TeamMember> findByIsActiveTrueOrderByDisplayOrderAsc();
    List<TeamMember> findByDepartmentIdOrderByDisplayOrderAsc(UUID departmentId);
}
