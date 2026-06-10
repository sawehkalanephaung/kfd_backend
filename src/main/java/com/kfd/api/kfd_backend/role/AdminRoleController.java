package com.kfd.api.kfd_backend.role;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiDataResponse<List<RoleResponseDTO>>> getAllRoles() {
        List<RoleResponseDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(new ApiDataResponse<>(HttpStatus.OK.value(), "Roles retrieved successfully", roles));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<ApiDataResponse<RoleResponseDTO>> getRoleById(@PathVariable UUID id) {
        RoleResponseDTO role = roleService.getRoleById(id);
        return ResponseEntity.ok(new ApiDataResponse<>(HttpStatus.OK.value(), "Role retrieved successfully", role));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiDataResponse<RoleResponseDTO>> createRole(@RequestBody RoleRequestDTO requestDTO) {
        RoleResponseDTO createdRole = roleService.createRole(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiDataResponse<>(HttpStatus.CREATED.value(), "Role created successfully", createdRole));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiDataResponse<RoleResponseDTO>> updateRole(@PathVariable UUID id, @RequestBody RoleRequestDTO requestDTO) {
        RoleResponseDTO updatedRole = roleService.updateRole(id, requestDTO);
        return ResponseEntity.ok(new ApiDataResponse<>(HttpStatus.OK.value(), "Role updated successfully", updatedRole));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiMessageResponse> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                new ApiMessageResponse(HttpStatus.NO_CONTENT.value(), "Role deleted successfully"));
    }
}
