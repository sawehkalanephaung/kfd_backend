package com.kfd.api.kfd_backend.faq;

import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FaqService {
    private final FaqRepository faqRepository;

    // get all faqs
    public List<Faq> getAllFaqs() {
        return faqRepository.findAll();
    }

    // get all publiced faqs
    public List<FaqDto> getPublicFaqs() {
        return faqRepository.findByStatusOrderByDisplayOrderAsc(FaqStatus.PUBLISHED)
                .stream()
                .map(faq -> FaqDto.builder()
                        .id(faq.getId())
                        .question(faq.getQuestion())
                        .answer(faq.getAnswer())
                        .displayOrder(faq.getDisplayOrder())
                        .status(faq.getStatus())
                        .build())
                .toList();
    }

    // Get by ID
    public Faq getFaqById(UUID id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ", "id", id));
    }

    @Transactional
    public Faq createFaq(FaqDto dto, UUID currentUserId) {
        Faq faq = Faq.builder()
                .question(dto.getQuestion())
                .answer(dto.getAnswer())
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .status(dto.getStatus() != null ? dto.getStatus() : FaqStatus.DRAFT)
                .createdBy(currentUserId)
                .lastUpdatedBy(currentUserId)
                .build();
        return faqRepository.save(faq);
    }

    @Transactional
    public Faq updateFaq(UUID id, FaqDto dto, UUID currentUserId) {
        Faq faq = getFaqById(id);
        faq.setQuestion(dto.getQuestion());
        faq.setAnswer(dto.getAnswer());
        faq.setDisplayOrder(dto.getDisplayOrder());
        faq.setStatus(dto.getStatus());
        faq.setLastUpdatedBy(currentUserId);
        return faqRepository.save(faq);
    }

    @Transactional
    public void deleteFaq(UUID id) {
        Faq faq = getFaqById(id);
        faqRepository.delete(faq);
    }

} // end of class
