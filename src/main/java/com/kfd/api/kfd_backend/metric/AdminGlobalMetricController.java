package com.kfd.api.kfd_backend.metric;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/metrics")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('ROLE_ADMIN')")
public class AdminGlobalMetricController {

    private final GlobalMetricService globalMetricService;

    @GetMapping
    public ResponseEntity<ApiDataResponse<List<GlobalMetricResponseDTO>>> getAllAdmin() {
        return ResponseEntity.ok(new ApiDataResponse<>(
                HttpStatus.OK.value(),
                "Metrics retrieved successfully",
                globalMetricService.getAllAdmin()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiDataResponse<GlobalMetricResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiDataResponse<>(
                HttpStatus.OK.value(),
                "Metric retrieved successfully",
                globalMetricService.getById(id)
        ));
    }

    @PostMapping
    public ResponseEntity<ApiDataResponse<GlobalMetricResponseDTO>> create(@Valid @RequestBody GlobalMetricRequestDTO request) {
        GlobalMetricResponseDTO created = globalMetricService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiDataResponse<>(
                HttpStatus.CREATED.value(),
                "Metric created successfully",
                created
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiDataResponse<GlobalMetricResponseDTO>> update(
            @PathVariable UUID id, 
            @Valid @RequestBody GlobalMetricRequestDTO request) {
        GlobalMetricResponseDTO updated = globalMetricService.update(id, request);
        return ResponseEntity.ok(new ApiDataResponse<>(
                HttpStatus.OK.value(),
                "Metric updated successfully",
                updated
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiMessageResponse> delete(@PathVariable UUID id) {
        globalMetricService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiMessageResponse(
                HttpStatus.NO_CONTENT.value(),
                "Metric deleted successfully"
        ));
    }
}
