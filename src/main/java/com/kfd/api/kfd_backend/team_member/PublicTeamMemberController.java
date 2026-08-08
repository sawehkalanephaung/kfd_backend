package com.kfd.api.kfd_backend.team_member;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Public team member endpoints — no authentication required.
 * Secured at path level by SecurityConfig: /api/v1/public/** is permitAll.
 */
@RestController
@RequestMapping("/api/v1/public/team-members")
@RequiredArgsConstructor
public class PublicTeamMemberController {

    private final TeamMemberService teamMemberService;

    /** GET /api/v1/public/team-members — list all active members */
    @GetMapping
    public ResponseEntity<ApiDataResponse<List<TeamMemberDto>>> getActiveMembers() {
        return ResponseEntity.ok(
                new ApiDataResponse<>(200, "Team members retrieved", teamMemberService.getActiveMembers()));
    }

    /** GET /api/v1/public/team-members/{id} — public profile with full bio */
    @GetMapping("/{id}")
    public ResponseEntity<ApiDataResponse<TeamMemberDto>> getMemberById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                new ApiDataResponse<>(200, "Team member retrieved", teamMemberService.getMemberById(id)));
    }
}
