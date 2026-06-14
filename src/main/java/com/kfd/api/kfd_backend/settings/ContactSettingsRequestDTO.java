package com.kfd.api.kfd_backend.settings;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ContactSettingsRequestDTO(
        String physicalAddress,

        @NotBlank(message = "Contact email is required")
        @Email(message = "Must be a valid email address")
        String contactEmail,

        @NotEmpty(message = "At least one inquiry type must be provided")
        List<String> inquiryTypes
) {}
