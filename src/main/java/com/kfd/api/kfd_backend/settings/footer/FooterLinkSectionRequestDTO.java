package com.kfd.api.kfd_backend.settings.footer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FooterLinkSectionRequestDTO(
        @NotBlank(message = "Section title is required")
        String title,

        @NotNull(message = "Display order is required")
        Integer displayOrder,

        Boolean isActive
) {}
