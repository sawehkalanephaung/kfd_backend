package com.kfd.api.kfd_backend.department;

import com.kfd.api.kfd_backend.team_member.TeamMember;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(nullable = false, length = 255)
    private String name;

    /**
     * The formal head of this department.
     * Must be an existing TeamMember record so they appear
     * on the staff list and have their own personal detail page.
     * Nullable — a department can exist before a head is assigned.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_member_id")
    private TeamMember headMember;

    @Column(name = "body_content", columnDefinition = "TEXT")
    private String bodyContent;

    @Column(name = "logo_id")
    private UUID logoId;

    @Column(name = "hero_image_id")
    private UUID heroImageId;

    @Builder.Default
    @Column(length = 50)
    private String status = "ACTIVE";

    @Builder.Default
    @Column(name = "order_index")
    private Integer orderIndex = 0;

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
