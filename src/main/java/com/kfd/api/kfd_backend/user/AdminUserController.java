package com.kfd.api.kfd_backend.user;

import com.kfd.api.kfd_backend.audit.AuditLogService;
import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<ApiDataResponse<UserResponseDTO>> getUserById(@PathVariable UUID id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiDataResponse<>(HttpStatus.OK.value(), "User retrieved successfully", user));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiDataResponse<UserResponseDTO>> createUser(
            @RequestBody UserRequestDTO requestDTO,
            Authentication auth,
            HttpServletRequest request) {

        UserResponseDTO createdUser = userService.createUser(requestDTO);
        auditLogService.log(resolveUserId(auth), "CREATE", "USER", createdUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiDataResponse<>(HttpStatus.CREATED.value(), "User created successfully", createdUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiDataResponse<UserResponseDTO>> updateUser(
            @PathVariable UUID id,
            @RequestBody UserRequestDTO requestDTO,
            Authentication auth,
            HttpServletRequest request) {

        UserResponseDTO updatedUser = userService.updateUser(id, requestDTO);
        auditLogService.log(resolveUserId(auth), "UPDATE", "USER", id, request);
        return ResponseEntity.ok(new ApiDataResponse<>(HttpStatus.OK.value(), "User updated successfully", updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiMessageResponse> deleteUser(
            @PathVariable UUID id,
            Authentication auth,
            HttpServletRequest request) {

        userService.deleteUser(id);
        auditLogService.log(resolveUserId(auth), "DELETE", "USER", id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                new ApiMessageResponse(HttpStatus.NO_CONTENT.value(), "User deleted successfully"));
    }

    // ─── Helper ──────────────────────────────────────────────────────────────────

    private UUID resolveUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof User u) {
            return u.getId();
        }
        return null;
    }
}

