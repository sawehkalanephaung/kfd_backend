package com.kfd.api.kfd_backend.global.exception;

/**
 * Standardized JSON success response body for operations that don't return data.
 * Used for DELETE, and other operations that need a simple confirmation message.
 *
 * Example JSON output:
 * {
 *   "status": 200,
 *   "message": "FAQ '20000000-0001-4000-8000-000000000001' was successfully deleted."
 * }
 */
public record ApiMessageResponse(
        int status,
        String message
) {}
