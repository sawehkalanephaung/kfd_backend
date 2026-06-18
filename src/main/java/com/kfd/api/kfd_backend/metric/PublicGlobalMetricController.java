package com.kfd.api.kfd_backend.metric;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/metrics")
@RequiredArgsConstructor
public class PublicGlobalMetricController {

    private final GlobalMetricService globalMetricService;

    @GetMapping
    public ResponseEntity<ApiDataResponse<List<GlobalMetricResponseDTO>>> getAllPublic() {
        return ResponseEntity.ok(new ApiDataResponse<>(
                HttpStatus.OK.value(),
                "Active metrics retrieved successfully",
                globalMetricService.getAllPublic()
        ));
    }
}
