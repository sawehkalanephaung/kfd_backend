package com.kfd.api.kfd_backend.settings.footer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FooterLinkRequestDTO(
        @NotBlank(message = "Link label is required")
        String label,

        @NotBlank(message = "Link URL is required")
        String url,

        @NotNull(message = "Display order is required")
        Integer displayOrder,

        Boolean isActive
) {}
