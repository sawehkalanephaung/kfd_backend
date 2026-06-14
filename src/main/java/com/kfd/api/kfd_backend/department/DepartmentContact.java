package com.kfd.api.kfd_backend.department;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "department_contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentContact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private String role;

    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "website_url", length = 1024)
    private String websiteUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "social_links", columnDefinition = "jsonb")
    private String socialLinks;

    @Column(name = "additional_details", columnDefinition = "TEXT")
    private String additionalDetails;

    @Builder.Default
    @Column(name = "order_index")
    private Integer orderIndex = 0;
}
