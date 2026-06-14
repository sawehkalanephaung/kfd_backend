package com.kfd.api.kfd_backend.inquiry;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "inquiries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "sender_name", nullable = false, length = 255)
    private String senderName;

    @Column(name = "sender_email", nullable = false, length = 255)
    private String senderEmail;

    @Column(name = "inquiry_type", length = 100)
    private String inquiryType;

    @Column(length = 512)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Builder.Default
    @Column(length = 50)
    private String status = "NEW";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
