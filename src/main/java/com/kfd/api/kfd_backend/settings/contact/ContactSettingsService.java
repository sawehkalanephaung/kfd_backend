package com.kfd.api.kfd_backend.settings.contact;

import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactSettingsService {

    private final ContactSettingsRepository repository;

    /**
     * Get all contact settings (usually there's only one, but supports CRUD)
     */
    @Transactional(readOnly = true)
    public List<ContactSettingsResponseDTO> getAllSettings() {
        return repository.findAll().stream()
                .map(ContactSettingsResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get the default/active contact setting.
     * Since there could be multiple, we just grab the first one, or throw if none exist.
     */
    @Transactional(readOnly = true)
    public ContactSettingsResponseDTO getDefaultSettings() {
        return repository.findAll().stream()
                .findFirst()
                .map(ContactSettingsResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("ContactSettings", "id", "default"));
    }

    /**
     * Get a specific setting by ID
     */
    @Transactional(readOnly = true)
    public ContactSettingsResponseDTO getSettingsById(UUID id) {
        return repository.findById(id)
                .map(ContactSettingsResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("ContactSettings", "id", id.toString()));
    }

    /**
     * Create a new contact setting
     */
    @Transactional
    public ContactSettingsResponseDTO createSettings(ContactSettingsRequestDTO dto) {
        ContactSettings settings = ContactSettings.builder()
                .physicalAddress(dto.physicalAddress())
                .contactEmail(dto.contactEmail())
                .inquiryTypes(dto.inquiryTypes())
                .phoneNumbers(dto.phoneNumbers())
                .build();

        ContactSettings saved = repository.save(settings);
        return ContactSettingsResponseDTO.fromEntity(saved);
    }

    /**
     * Update an existing contact setting
     */
    @Transactional
    public ContactSettingsResponseDTO updateSettings(UUID id, ContactSettingsRequestDTO dto) {
        ContactSettings existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactSettings", "id", id.toString()));

        existing.setPhysicalAddress(dto.physicalAddress());
        existing.setContactEmail(dto.contactEmail());
        existing.setInquiryTypes(dto.inquiryTypes());
        existing.setPhoneNumbers(dto.phoneNumbers());

        ContactSettings updated = repository.save(existing);
        return ContactSettingsResponseDTO.fromEntity(updated);
    }

    /**
     * Delete a contact setting
     */
    @Transactional
    public void deleteSettings(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("ContactSettings", "id", id.toString());
        }
        repository.deleteById(id);
    }
}
