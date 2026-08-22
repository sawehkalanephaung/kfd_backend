package com.kfd.api.kfd_backend.global.web;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Resolves the real client IP for a request.
 *
 * The application runs behind the Elastic Beanstalk nginx proxy, so
 * {@code request.getRemoteAddr()} alone reports the proxy's address, not the
 * caller's. Anything that keys behaviour on client identity — audit logging,
 * rate limiting — must go through here instead.
 */
public final class ClientIpResolver {

    private ClientIpResolver() {
    }

    public static String resolve(HttpServletRequest request) {
        if (request == null) return null;

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            // X-Forwarded-For can be a comma-separated list; the first IP is the original client.
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
