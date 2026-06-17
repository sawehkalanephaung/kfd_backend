package com.kfd.api.kfd_backend.audit;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * JPA Entity for the audit_logs table.
 * Records who did what, to which entity, and when.
 */
@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** The admin user who performed the action (null for unauthenticated events like login failure). */
    @Column(name = "user_id")
    private UUID userId;

    /**
     * Type of action: LOGIN, LOGOUT, CREATE, UPDATE, DELETE.
     */
    @Column(name = "action_type", length = 255)
    private String actionType;

    /**
     * The entity/domain that was affected: USER, ROLE, FAQ, TEAM_MEMBER,
     * DEPARTMENT, PAGE, POST, MEDIA, CONTACT_SETTINGS, etc.
     */
    @Column(name = "entity_type", length = 255)
    private String entityType;

    /** The UUID of the specific record that was affected (null for LOGIN events). */
    @Column(name = "entity_id")
    private UUID entityId;

    /** Client IP address extracted from the HTTP request. */
    @Column(name = "ip_address", length = 255)
    private String ipAddress;

    /** User-Agent string from the HTTP request header. */
    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
