package com.kfd.api.kfd_backend.faq;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table( name = "faqs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 1024)
    private String question;

    @Column(nullable = false, length = 4096)
    private String answer;

    @Column(name = "display_order")
    private Integer displayOrder;

    // status by default ( draft and published )
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private FaqStatus status =  FaqStatus.DRAFT;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "last_updated_by")
    private UUID lastUpdatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;







}
