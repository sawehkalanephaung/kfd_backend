package com.kfd.api.kfd_backend.settings.contact;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ContactSettingsResponseDTO(
        UUID id,
        String physicalAddress,
        String contactEmail,
        List<String> inquiryTypes,
        List<String> phoneNumbers,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ContactSettingsResponseDTO fromEntity(ContactSettings settings) {
        if (settings == null) {
            return null;
        }
        return new ContactSettingsResponseDTO(
                settings.getId(),
                settings.getPhysicalAddress(),
                settings.getContactEmail(),
                settings.getInquiryTypes(),
                settings.getPhoneNumbers(),
                settings.getCreatedAt(),
                settings.getUpdatedAt()
        );
    }
}
