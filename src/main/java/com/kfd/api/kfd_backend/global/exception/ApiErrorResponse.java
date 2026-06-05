package com.kfd.api.kfd_backend.global.exception;

/**
 * Standardized JSON error response body for all API errors.
 *
 * Uses a Java 21 Record for immutability and conciseness.
 *
 * Example JSON output:
 * {
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "FAQ not found with id: 'abc-123'"
 * }
 */
public record ApiErrorResponse(
        int status,
        String error,
        String message
) {}
