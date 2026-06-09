package com.kfd.api.kfd_backend.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when trying to delete a resource that is currently in use (referenced by other entities).
 * Automatically maps to HTTP 409 Conflict.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceInUseException extends RuntimeException {

    public ResourceInUseException(String message) {
        super(message);
    }

    public ResourceInUseException(String resourceName, String referencedBy) {
        super(String.format("%s cannot be deleted because it is being used by %s.", resourceName, referencedBy));
    }
}
