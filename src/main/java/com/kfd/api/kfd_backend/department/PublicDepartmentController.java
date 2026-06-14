package com.kfd.api.kfd_backend.department;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/departments")
@RequiredArgsConstructor
public class PublicDepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<ApiDataResponse<List<DepartmentPublicResponseDTO>>> getAll() {
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Departments retrieved successfully", departmentService.getAllPublic()));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiDataResponse<DepartmentPublicResponseDTO>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Department retrieved successfully", departmentService.getBySlugPublic(slug)));
    }
}
