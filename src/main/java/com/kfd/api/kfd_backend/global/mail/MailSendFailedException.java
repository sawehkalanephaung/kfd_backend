package com.kfd.api.kfd_backend.global.mail;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MailSendFailedException extends RuntimeException {
    public MailSendFailedException(String message) {
        super(message);
    }
}
