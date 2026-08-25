package com.kfd.api.kfd_backend.auth;

import com.kfd.api.kfd_backend.global.web.ClientIpResolver;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Throttles the unauthenticated auth endpoints.
 *
 * {@code /api/v1/auth/**} is permitAll, which leaves login open to credential
 * brute-forcing and forgot-password open to mail-spamming a victim's inbox.
 * This filter caps both per client IP.
 *
 * <p><b>Scaling note:</b> buckets are held in memory, which is correct while the
 * environment runs a single Elastic Beanstalk instance. If this is ever scaled
 * horizontally, each instance would keep its own counters and the effective limit
 * would multiply by the instance count — at that point this must move to a shared
 * store (Redis via bucket4j-redis).
 */
@Slf4j
@Component
public class AuthRateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/api/v1/auth/login";
    private static final String FORGOT_PASSWORD_PATH = "/api/v1/auth/forgot-password";

    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> forgotPasswordBuckets = new ConcurrentHashMap<>();

    @Value("${app.ratelimit.login.capacity:10}")
    private int loginCapacity;

    @Value("${app.ratelimit.login.window-minutes:1}")
    private int loginWindowMinutes;

    @Value("${app.ratelimit.forgot-password.capacity:3}")
    private int forgotPasswordCapacity;

    @Value("${app.ratelimit.forgot-password.window-minutes:60}")
    private int forgotPasswordWindowMinutes;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Only the two unauthenticated, abuse-prone endpoints are throttled.
        String path = request.getServletPath();
        boolean isTargetPath = LOGIN_PATH.equals(path) || FORGOT_PASSWORD_PATH.equals(path);
        return !isTargetPath || !"POST".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        String clientIp = ClientIpResolver.resolve(request);
        String key = clientIp == null ? "unknown" : clientIp;

        Bucket bucket = LOGIN_PATH.equals(path)
                ? loginBuckets.computeIfAbsent(key,
                        k -> newBucket(loginCapacity, loginWindowMinutes))
                : forgotPasswordBuckets.computeIfAbsent(key,
                        k -> newBucket(forgotPasswordCapacity, forgotPasswordWindowMinutes));

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
            return;
        }

        log.warn("Rate limit exceeded for {} from ip={}", path, key);
        writeTooManyRequests(response);
    }

    private Bucket newBucket(int capacity, int windowMinutes) {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(capacity)
                        .refillIntervally(capacity, Duration.ofMinutes(windowMinutes))
                        .build())
                .build();
    }

    private void writeTooManyRequests(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
                {"status":429,"error":"Too Many Requests",\
                "message":"Too many attempts. Please wait a few minutes and try again."}""");
    }
}
