package com.kfd.api.kfd_backend.global.mail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Maps to the Resend API request payload.
 * @see <a href="https://resend.com/docs/api-reference/emails/send-email">Resend API Docs</a>
 */
public record ResendEmailRequest(
        @JsonProperty("from") String from,
        @JsonProperty("to") List<String> to,
        @JsonProperty("subject") String subject,
        @JsonProperty("html") String html,
        @JsonProperty("reply_to") String replyTo
) {}
