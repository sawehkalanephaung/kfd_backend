package com.kfd.api.kfd_backend.inquiry;

import java.util.List;

/**
 * Maps to the Resend API request payload.
 * @see <a href="https://resend.com/docs/api-reference/emails/send-email">Resend API Docs</a>
 */
public record ResendEmailRequest(
        String from,
        List<String> to,
        String subject,
        String html,
        String reply_to
) {}
