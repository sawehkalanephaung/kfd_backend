package com.kfd.api.kfd_backend.cms.publication;

import com.kfd.api.kfd_backend.audit.AuditHelper;
import com.kfd.api.kfd_backend.audit.AuditLogService;
import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/admin/cms/publications")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('manage_content') or hasAuthority('ROLE_SUPER_ADMIN')")
public class PublicationController {

    private final PublicationService publicationService;
    private final AuditLogService auditLogService;
    private final AuditHelper auditHelper;

    /**
     * GET /api/v1/admin/cms/publications?page=0&size=10&search=xyz&category=Reports
     * Returns all publications (DRAFT, PUBLISHED, ARCHIVED) paginated for the admin table.
     */
    @GetMapping
    public ResponseEntity<Page<PublicationResponseDto>> getAllPublications(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(publicationService.getAllPublications(search, category, status, pageable));
    }

    /**
     * GET /api/v1/admin/cms/publications/{id}
     * Returns full publication details by UUID — used to populate the edit form.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PublicationResponseDto> getPublicationById(@PathVariable UUID id) {
        return ResponseEntity.ok(publicationService.getPublicationById(id));
    }

    /**
     * POST /api/v1/admin/cms/publications
     * Creates a new publication. Defaults to DRAFT if no status is provided.
     */
    @PostMapping
    public ResponseEntity<ApiDataResponse<PublicationResponseDto>> createPublication(
            @Valid @RequestBody PublicationRequestDto dto,
            HttpServletRequest request) {
        PublicationResponseDto created = publicationService.createPublication(dto);
        auditLogService.log(auditHelper.getCurrentUserId(), "CREATE", "PUBLICATION", created.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiDataResponse<>(
                        HttpStatus.CREATED.value(),
                        String.format("Publication '%s' was successfully created.", created.getTitle()),
                        created));
    }

    /**
     * PUT /api/v1/admin/cms/publications/{id}
     * Updates an existing publication.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiDataResponse<PublicationResponseDto>> updatePublication(
            @PathVariable UUID id,
            @Valid @RequestBody PublicationRequestDto dto,
            HttpServletRequest request) {
        PublicationResponseDto updated = publicationService.updatePublication(id, dto);
        auditLogService.log(auditHelper.getCurrentUserId(), "UPDATE", "PUBLICATION", id, request);
        return ResponseEntity.ok(
                new ApiDataResponse<>(
                        HttpStatus.OK.value(),
                        String.format("Publication '%s' was successfully updated.", updated.getTitle()),
                        updated));
    }

    /**
     * DELETE /api/v1/admin/cms/publications/{id}
     * Soft-deletes a publication by setting status to ARCHIVED.
     * If the publication is already ARCHIVED, permanently hard-deletes it.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiMessageResponse> deleteOrArchivePublication(
            @PathVariable UUID id,
            HttpServletRequest request) {
        boolean isPermanentlyDeleted = publicationService.deleteOrArchivePublication(id);
        String actionType = isPermanentlyDeleted ? "DELETE" : "ARCHIVE";
        String message = isPermanentlyDeleted
                ? String.format("Publication with ID '%s' was permanently deleted.", id)
                : String.format("Publication with ID '%s' was successfully archived.", id);
        auditLogService.log(auditHelper.getCurrentUserId(), actionType, "PUBLICATION", id, request);
        return ResponseEntity.ok(new ApiMessageResponse(HttpStatus.OK.value(), message));
    }
}
