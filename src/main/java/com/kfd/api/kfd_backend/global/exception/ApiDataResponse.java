package com.kfd.api.kfd_backend.global.exception;

/**
 * Standardized JSON response for operations that return data with a message.
 * Used for CREATE (POST) and UPDATE (PUT) operations.
 *
 * Example JSON output for a FAQ creation:
 * {
 *   "status": 201,
 *   "message": "FAQ was successfully created.",
 *   "data": {
 *     "id": "...",
 *     "question": "...",
 *     ...
 *   }
 * }
 *
 * @param <T> The type of the returned data object (e.g., Faq, TeamMember)
 */
public record ApiDataResponse<T>(
        int status,
        String message,
        T data
) {}
