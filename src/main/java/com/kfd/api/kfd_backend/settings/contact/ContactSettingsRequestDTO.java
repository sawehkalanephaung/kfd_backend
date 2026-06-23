package com.kfd.api.kfd_backend.settings.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ContactSettingsRequestDTO(
        String physicalAddress,

        @NotBlank(message = "Contact email is required")
        @Email(message = "Must be a valid email address")
        String contactEmail,

        List<String> inquiryTypes,
        
        List<String> phoneNumbers,

        String officeHours
) {}
