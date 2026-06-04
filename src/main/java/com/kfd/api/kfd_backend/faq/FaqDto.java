package com.kfd.api.kfd_backend.faq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqDto {
    private UUID id;
    private String question;
    private String answer;
    private Integer displayOrder;
    private FaqStatus status;
}
