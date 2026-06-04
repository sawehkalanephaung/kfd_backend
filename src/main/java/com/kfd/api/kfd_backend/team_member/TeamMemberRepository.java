package com.kfd.api.kfd_backend.team_member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
    // Fetch only active members, ordered by display_order
    List<TeamMember> findByIsActiveTrueOrderByDisplayOrderAsc();
}

