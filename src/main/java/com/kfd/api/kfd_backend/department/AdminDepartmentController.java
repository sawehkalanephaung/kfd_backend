package com.kfd.api.kfd_backend.department;

import com.kfd.api.kfd_backend.audit.AuditHelper;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/departments")
@RequiredArgsConstructor
public class AdminDepartmentController {

    private final DepartmentService departmentService;
    private final AuditLogService auditLogService;
    private final AuditHelper auditHelper;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<Page<DepartmentAdminResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(departmentService.getAllAdmin(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<ApiDataResponse<DepartmentAdminResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                new ApiDataResponse<>(200, "Department retrieved successfully", departmentService.getByIdAdmin(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<DepartmentAdminResponseDTO>> create(
            @RequestBody DepartmentAdminRequestDTO dto,
            HttpServletRequest request) {
        DepartmentAdminResponseDTO created = departmentService.create(dto);
        auditLogService.log(auditHelper.getCurrentUserId(), "CREATE", "DEPARTMENT", created.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiDataResponse<>(201, "Department created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<DepartmentAdminResponseDTO>> update(
            @PathVariable UUID id,
            @RequestBody DepartmentAdminRequestDTO dto,
            HttpServletRequest request) {
        DepartmentAdminResponseDTO updated = departmentService.update(id, dto);
        auditLogService.log(auditHelper.getCurrentUserId(), "UPDATE", "DEPARTMENT", id, request);
        return ResponseEntity
                .ok(new ApiDataResponse<>(200, "Department updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiMessageResponse> delete(
            @PathVariable UUID id,
            HttpServletRequest request) {
        departmentService.delete(id);
        auditLogService.log(auditHelper.getCurrentUserId(), "DELETE", "DEPARTMENT", id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new ApiMessageResponse(204, "Department deleted successfully"));
    }

    // ─── Contact sub-resource ────────────────────────────────────────────────────

    @GetMapping("/{id}/contacts")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<ApiDataResponse<List<DepartmentContactResponseDTO>>> getContacts(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Contacts retrieved", departmentService.getContacts(id)));
    }

    @PostMapping("/{id}/contacts")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<DepartmentContactResponseDTO>> addContact(
            @PathVariable UUID id,
            @RequestBody DepartmentContactRequestDTO dto,
            HttpServletRequest request) {
        DepartmentContactResponseDTO created = departmentService.addContact(id, dto);
        auditLogService.log(auditHelper.getCurrentUserId(), "ADD_CONTACT", "DEPARTMENT", id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiDataResponse<>(201, "Contact added successfully", created));
    }

    @PutMapping("/contacts/{contactId}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<DepartmentContactResponseDTO>> updateContact(
            @PathVariable UUID contactId,
            @RequestBody DepartmentContactRequestDTO dto,
            HttpServletRequest request) {
        DepartmentContactResponseDTO updated = departmentService.updateContact(contactId, dto);
        auditLogService.log(auditHelper.getCurrentUserId(), "UPDATE_CONTACT", "DEPARTMENT", contactId, request);
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Contact updated successfully", updated));
    }

    @DeleteMapping("/contacts/{contactId}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiMessageResponse> deleteContact(
            @PathVariable UUID contactId,
            HttpServletRequest request) {
        departmentService.deleteContact(contactId);
        auditLogService.log(auditHelper.getCurrentUserId(), "DELETE_CONTACT", "DEPARTMENT", contactId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new ApiMessageResponse(204, "Contact deleted successfully"));
    }
}
