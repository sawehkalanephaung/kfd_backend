package com.kfd.api.kfd_backend.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an operation would leave the system without any active Super Admin.
 *
 * This guards the "everyone is locked out" scenario at its most common source: an
 * administrator deleting, deactivating, or demoting the last remaining Super Admin.
 * Recovering from that state requires the out-of-band break-glass procedure
 * (see ops/breakglass/), so it is worth refusing the operation up front.
 *
 * Automatically maps to HTTP 409 Conflict.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class LastSuperAdminException extends RuntimeException {

    public LastSuperAdminException(String message) {
        super(message);
    }
}
