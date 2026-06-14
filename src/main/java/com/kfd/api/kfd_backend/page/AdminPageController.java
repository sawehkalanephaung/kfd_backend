package com.kfd.api.kfd_backend.page;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/pages")
@RequiredArgsConstructor
public class AdminPageController {

    private final PageService pageService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<org.springframework.data.domain.Page<PageResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(pageService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_EDITOR')")
    public ResponseEntity<ApiDataResponse<PageResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Page retrieved", pageService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<PageResponseDTO>> create(@RequestBody PageRequestDTO dto, Authentication auth) {
        UUID userId = resolveUserId(auth);
        PageResponseDTO created = pageService.create(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiDataResponse<>(201, "Page created", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<ApiDataResponse<PageResponseDTO>> update(@PathVariable UUID id, @RequestBody PageRequestDTO dto, Authentication auth) {
        UUID userId = resolveUserId(auth);
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Page updated", pageService.update(id, dto, userId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiMessageResponse> delete(@PathVariable UUID id) {
        pageService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiMessageResponse(204, "Page deleted"));
    }

    private UUID resolveUserId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof com.kfd.api.kfd_backend.user.User u) {
            return u.getId();
        }
        return null;
    }
}
