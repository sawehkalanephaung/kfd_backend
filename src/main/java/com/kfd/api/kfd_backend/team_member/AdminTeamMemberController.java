package com.kfd.api.kfd_backend.team_member;

import com.kfd.api.kfd_backend.audit.AuditHelper;
import com.kfd.api.kfd_backend.audit.AuditLogService;
import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Admin team member endpoints — all routes require authentication.
 * Secured at path level by SecurityConfig: /api/v1/admin/** requires authenticated().
 * Fine-grained role checks enforced via @PreAuthorize.
 */
@RestController
@RequestMapping("/api/v1/admin/team-members")
@RequiredArgsConstructor
public class AdminTeamMemberController {

    private final TeamMemberService teamMemberService;
    private final AuditLogService auditLogService;
    private final AuditHelper auditHelper;

    /** GET /api/v1/admin/team-members — list all members including inactive */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<ApiDataResponse<List<TeamMemberDto>>> getAllMembers() {
        return ResponseEntity.ok(
                new ApiDataResponse<>(200, "All team members retrieved", teamMemberService.getAllMembers()));
    }

    /** GET /api/v1/admin/team-members/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<ApiDataResponse<TeamMemberDto>> getMemberById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                new ApiDataResponse<>(200, "Team member retrieved", teamMemberService.getMemberById(id)));
    }

    /** POST /api/v1/admin/team-members */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<TeamMemberDto>> createMember(
            @RequestBody TeamMemberDto dto,
            HttpServletRequest request) {
        UUID currentUserId = auditHelper.getCurrentUserId();
        TeamMemberDto created = teamMemberService.createMember(dto, currentUserId);
        auditLogService.log(currentUserId, "CREATE", "TEAM_MEMBER", created.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiDataResponse<>(201, "Team member created", created));
    }

    /** PUT /api/v1/admin/team-members/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<TeamMemberDto>> updateMember(
            @PathVariable UUID id,
            @RequestBody TeamMemberDto dto,
            HttpServletRequest request) {
        UUID currentUserId = auditHelper.getCurrentUserId();
        TeamMemberDto updated = teamMemberService.updateMember(id, dto, currentUserId);
        auditLogService.log(currentUserId, "UPDATE", "TEAM_MEMBER", id, request);
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Team member updated", updated));
    }

    /** DELETE /api/v1/admin/team-members/{id} */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiMessageResponse> deleteMember(
            @PathVariable UUID id,
            HttpServletRequest request) {
        teamMemberService.deleteMember(id);
        auditLogService.log(auditHelper.getCurrentUserId(), "DELETE", "TEAM_MEMBER", id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new ApiMessageResponse(204, "Team member deleted"));
    }
}
