package com.kfd.api.kfd_backend.team_member;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/team-members")
@RequiredArgsConstructor
public class TeamMemberController {
    private final TeamMemberService teamMemberService;

    // Public: Get active members ordered by display_order
    @GetMapping
    public ResponseEntity<List<TeamMemberDto>> getActiveMembers() {
        return ResponseEntity.ok(teamMemberService.getActiveMembers());
    }

    // Admin: Get all members (including inactive)
    @GetMapping("/admin")
    public ResponseEntity<List<TeamMember>> getAllMembers() {
        return ResponseEntity.ok(teamMemberService.getAllMembers());
    }

    // Get single member by ID
    @GetMapping("/{id}")
    public ResponseEntity<TeamMember> getMemberById(@PathVariable UUID id) {
        return ResponseEntity.ok(teamMemberService.getMemberById(id));
    }

    // Create new member
    @PostMapping
    public ResponseEntity<TeamMember> createMember(@RequestBody TeamMemberDto dto) {
        // Placeholder admin ID — will be replaced by real auth later
        UUID mockAdminId = UUID.fromString("d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80");
        TeamMember created = teamMemberService.createMember(dto, mockAdminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Update existing member
    @PutMapping("/{id}")
    public ResponseEntity<TeamMember> updateMember(@PathVariable UUID id,
            @RequestBody TeamMemberDto dto) {
        UUID mockAdminId = UUID.fromString("d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80");
        TeamMember updated = teamMemberService.updateMember(id, dto, mockAdminId);
        return ResponseEntity.ok(updated);
    }

    // Delete a member
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        teamMemberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

}
