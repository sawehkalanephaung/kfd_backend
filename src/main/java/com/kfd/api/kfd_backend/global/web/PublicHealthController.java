package com.kfd.api.kfd_backend.global.web;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Target for the Elastic Beanstalk / ALB health check.
 *
 * Deliberately shallow — it touches no database and no downstream service. The
 * load balancer uses this to decide whether to route traffic at all, so a
 * dependency outage must not mark the instance unhealthy: that turns a
 * recoverable blip into a total outage and can trigger instance replacement
 * that does nothing to fix the actual cause. Watch dependencies with their own
 * alarms instead.
 *
 * Lives under /api/v1/public/** so the existing permitAll rule in SecurityConfig
 * already covers it; no security change is needed to keep it reachable.
 */
@RestController
@RequestMapping("/api/v1/public/health")
public class PublicHealthController {

    @GetMapping
    public ResponseEntity<ApiDataResponse<String>> health() {
        return ResponseEntity.ok(new ApiDataResponse<>(200, "Service is healthy", "UP"));
    }
}
