package com.kfd.api.kfd_backend.settings.social;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialMediaLinkRequestDTO(
        @NotBlank(message = "Platform name is required")
        String platformName,

        @NotBlank(message = "URL is required")
        String url,

        @NotNull(message = "Display order is required")
        Integer displayOrder,

        @NotNull(message = "Active status is required")
        Boolean isActive
) {}
