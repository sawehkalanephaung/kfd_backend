package com.kfd.api.kfd_backend.inquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponseDTO {
    private UUID id;
    private String senderName;
    private String senderEmail;
    private String inquiryType;
    private String subject;
    private String message;
    private String status;
    private OffsetDateTime createdAt;
}
