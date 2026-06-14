package com.kfd.api.kfd_backend.inquiry;

import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    private InquiryResponseDTO toDto(Inquiry i) {
        return InquiryResponseDTO.builder()
                .id(i.getId())
                .senderName(i.getSenderName())
                .senderEmail(i.getSenderEmail())
                .inquiryType(i.getInquiryType())
                .subject(i.getSubject())
                .message(i.getMessage())
                .status(i.getStatus())
                .createdAt(i.getCreatedAt())
                .build();
    }

    @Transactional
    public InquiryResponseDTO submit(InquiryRequestDTO dto) {
        Inquiry inquiry = Inquiry.builder()
                .senderName(dto.getSenderName())
                .senderEmail(dto.getSenderEmail())
                .inquiryType(dto.getInquiryType())
                .subject(dto.getSubject())
                .message(dto.getMessage())
                .status("NEW")
                .build();
        return toDto(inquiryRepository.save(inquiry));
    }

    public Page<InquiryResponseDTO> getAll(Pageable pageable) {
        return inquiryRepository.findAll(pageable).map(this::toDto);
    }

    public InquiryResponseDTO getById(UUID id) {
        return toDto(inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry", "id", id)));
    }

    @Transactional
    public InquiryResponseDTO updateStatus(UUID id, String status) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry", "id", id));
        inquiry.setStatus(status);
        return toDto(inquiryRepository.save(inquiry));
    }
    @Transactional
    public void delete(UUID id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry", "id", id));
        inquiryRepository.delete(inquiry);
    }
}
