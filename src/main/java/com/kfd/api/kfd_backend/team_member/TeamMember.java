package com.kfd.api.kfd_backend.team_member;

import com.kfd.api.kfd_backend.department.Department;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "team_members")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 255)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "title", columnDefinition = "jsonb")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "bio", columnDefinition = "jsonb")
    private String bio;

    @Column(name = "headshot_url", length = 1024)
    private String headshotUrl;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "last_updated_by")
    private UUID lastUpdatedBy;
}
