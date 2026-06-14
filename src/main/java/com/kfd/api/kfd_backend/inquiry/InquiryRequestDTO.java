package com.kfd.api.kfd_backend.inquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryRequestDTO {
    private String senderName;
    private String senderEmail;
    private String inquiryType;
    private String subject;
    private String message;
}
