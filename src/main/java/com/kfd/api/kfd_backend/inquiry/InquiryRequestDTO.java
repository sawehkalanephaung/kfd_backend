package com.kfd.api.kfd_backend.inquiry;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InquiryRequestDTO(
        @NotBlank(message = "Sender name is required")
        String senderName,

        @NotBlank(message = "Sender email is required")
        @Email(message = "Sender email must be a valid email address")
        String senderEmail,

        @NotBlank(message = "Inquiry type is required")
        String inquiryType,

        @NotBlank(message = "Subject is required")
        String subject,

        @NotBlank(message = "Message is required")
        String message
) {}
