package com.kfd.api.kfd_backend.settings.identity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The organization's public identity — brand name, tagline, logo and footer line.
 *
 * Effectively a singleton: the public site reads one record. It is modelled as a
 * normal table rather than a key/value store to match the sibling settings
 * entities (see {@code ContactSettings}).
 */
@Entity
@Table(name = "site_identity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    /** S'gaw Karen name, shown beneath the English one in the header and footer. */
    @Column(name = "organization_name_karen")
    private String organizationNameKaren;

    @Column(name = "tagline")
    private String tagline;

    @Column(name = "logo_url", length = 1024)
    private String logoUrl;

    @Column(name = "footer_copyright")
    private String footerCopyright;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
