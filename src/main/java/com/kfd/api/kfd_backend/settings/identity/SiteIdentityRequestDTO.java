package com.kfd.api.kfd_backend.settings.identity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SiteIdentityRequestDTO(

        @NotBlank(message = "Organization name is required.")
        @Size(max = 255, message = "Organization name must be at most 255 characters.")
        String organizationName,

        @Size(max = 255, message = "Karen organization name must be at most 255 characters.")
        String organizationNameKaren,

        @Size(max = 255, message = "Tagline must be at most 255 characters.")
        String tagline,

        @Size(max = 1024, message = "Logo URL must be at most 1024 characters.")
        String logoUrl,

        @Size(max = 255, message = "Footer copyright must be at most 255 characters.")
        String footerCopyright
) {}
