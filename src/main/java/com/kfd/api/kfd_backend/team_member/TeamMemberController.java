package com.kfd.api.kfd_backend.team_member;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @GetMapping("/public/team-members")
    public ResponseEntity<ApiDataResponse<List<TeamMemberDto>>> getActiveMembers() {
        return ResponseEntity
                .ok(new ApiDataResponse<>(200, "Team members retrieved", teamMemberService.getActiveMembers()));
    }

    /** Public profile page for a single KFD member — includes full bio. */
    @GetMapping("/public/team-members/{id}")
    public ResponseEntity<ApiDataResponse<TeamMemberDto>> getMemberPublic(@PathVariable UUID id) {
        return ResponseEntity
                .ok(new ApiDataResponse<>(200, "Team member retrieved", teamMemberService.getMemberById(id)));
    }

    @GetMapping("/admin/team-members")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<ApiDataResponse<List<TeamMemberDto>>> getAllMembers() {
        return ResponseEntity
                .ok(new ApiDataResponse<>(200, "All team members retrieved", teamMemberService.getAllMembers()));
    }

    @GetMapping("/admin/team-members/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<ApiDataResponse<TeamMemberDto>> getMemberById(@PathVariable UUID id) {
        return ResponseEntity
                .ok(new ApiDataResponse<>(200, "Team member retrieved", teamMemberService.getMemberById(id)));
    }

    @PostMapping("/admin/team-members")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<TeamMemberDto>> createMember(@RequestBody TeamMemberDto dto,
            Authentication auth) {
        UUID currentUserId = resolveUserId(auth);
        TeamMemberDto created = teamMemberService.createMember(dto, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiDataResponse<>(201, "Team member created", created));
    }

    @PutMapping("/admin/team-members/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<TeamMemberDto>> updateMember(@PathVariable UUID id,
            @RequestBody TeamMemberDto dto, Authentication auth) {
        UUID currentUserId = resolveUserId(auth);
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Team member updated",
                teamMemberService.updateMember(id, dto, currentUserId)));
    }

    @DeleteMapping("/admin/team-members/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiMessageResponse> deleteMember(@PathVariable UUID id) {
        teamMemberService.deleteMember(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiMessageResponse(204, "Team member deleted"));
    }

    private UUID resolveUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof com.kfd.api.kfd_backend.user.User u) {
            return u.getId();
        }
        return null;
    }
}
