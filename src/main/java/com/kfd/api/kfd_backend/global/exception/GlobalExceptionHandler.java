package com.kfd.api.kfd_backend.global.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Centralized exception handler for all REST controllers in the KFD backend.
 *
 * @RestControllerAdvice intercepts exceptions thrown from any @RestController
 *                       and returns a clean, standardized ApiErrorResponse JSON
 *                       instead of Spring's
 *                       default verbose error page.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException → HTTP 404 Not Found
     * Triggered when: a FAQ, TeamMember, or any resource ID does not exist.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(), // 404
                "Not Found",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles DuplicateResourceException → HTTP 409 Conflict
     * Triggered when: trying to register a user with an already-existing email.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResource(DuplicateResourceException ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(), // 409
                "Conflict",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Handles ResourceInUseException → HTTP 409 Conflict
     */
    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceInUse(ResourceInUseException ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(), // 409
                "Conflict",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Handles invalid path variable types → HTTP 400 Bad Request
     * Triggered when: a non-UUID string is passed as a UUID path variable.
     * Example: GET /api/faqs/not-a-valid-uuid → 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected a valid UUID format (e.g. xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx).",
                ex.getValue(), ex.getName());
        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(), // 400
                "Bad Request",
                message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles FK constraint violations → HTTP 409 Conflict
     * Triggered when: trying to delete a record that is still referenced by another
     * table.
     * Example: deleting a user that is set as created_by in a FAQ record.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(), // 409
                "Conflict",
                "Cannot delete this record because it is still referenced by other data in the system.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Handles MaxUploadSizeExceededException → HTTP 413 Payload Too Large
     */
    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxSizeException(
            org.springframework.web.multipart.MaxUploadSizeExceededException ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.PAYLOAD_TOO_LARGE.value(), // 413
                "Payload Too Large",
                "File size exceeds the configured limit of 5MB.");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(body);
    }

    /**
     * Handles StorageException → HTTP 500 Internal Server Error
     */
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ApiErrorResponse> handleStorageException(StorageException ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500
                "Storage Error",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * Handles MissingServletRequestParameterException and
     * MissingServletRequestPartException → HTTP 400 Bad Request
     */
    @ExceptionHandler({
            org.springframework.web.bind.MissingServletRequestParameterException.class,
            org.springframework.web.multipart.support.MissingServletRequestPartException.class
    })
    public ResponseEntity<ApiErrorResponse> handleMissingParams(Exception ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(), // 400
                "Bad Request",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles NoResourceFoundException → HTTP 404 Not Found
     * Triggered when: a client requests a URL path that does not exist.
     * Prevents Spring Boot 3.2+ from throwing a 500 for missing endpoints.
     */
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFound(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(), // 404
                "Not Found",
                "The requested endpoint does not exist: " + ex.getResourcePath());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles MailSendFailedException → HTTP 503 Service Unavailable
     * Triggered when: email delivery via Resend API fails (e.g., bad API key, rate limit).
     */
    @ExceptionHandler(com.kfd.api.kfd_backend.global.mail.MailSendFailedException.class)
    public ResponseEntity<ApiErrorResponse> handleMailSendFailedException(
            com.kfd.api.kfd_backend.global.mail.MailSendFailedException ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(), // 503
                "Service Unavailable",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    /**
     * Handles Bean Validation errors → HTTP 400 Bad Request
     * Triggered when: @Valid fails on a @RequestBody (e.g., missing @NotBlank fields).
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String firstError = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("Validation failed");
        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(), // 400
                "Bad Request",
                firstError);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles Spring Security Access/Authorization Denied exceptions → HTTP 403 Forbidden
     * Triggered when: a user with a valid token tries to access a @PreAuthorize endpoint they don't have roles for.
     */
    @ExceptionHandler({
            org.springframework.security.access.AccessDeniedException.class,
            org.springframework.security.authorization.AuthorizationDeniedException.class
    })
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(Exception ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.FORBIDDEN.value(), // 403
                "Forbidden",
                "You do not have permission to access this resource.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Fallback handler for any unexpected exception → HTTP 500 Internal Server Error
     * Prevents leaking internal stack traces to the API consumer.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        // Log the actual exception for debugging
        ex.printStackTrace();

        ApiErrorResponse body = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500
                "Internal Server Error",
                "An unexpected error occurred. Please contact support.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
