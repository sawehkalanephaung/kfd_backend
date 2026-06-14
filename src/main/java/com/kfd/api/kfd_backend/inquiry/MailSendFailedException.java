package com.kfd.api.kfd_backend.inquiry;

/**
 * Thrown when the mail service fails to send an email via SMTP.
 * Caught by GlobalExceptionHandler and returned as a 503 Service Unavailable.
 */
public class MailSendFailedException extends RuntimeException {

    public MailSendFailedException(String message) {
        super(message);
    }

    public MailSendFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
