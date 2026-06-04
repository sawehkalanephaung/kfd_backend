package com.kfd.api.kfd_backend.faq;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class FaqDto {
    private UUID id;
    private String question;
    private String answer;
    private Integer displayOrder;
    private FaqStatus status;
}
