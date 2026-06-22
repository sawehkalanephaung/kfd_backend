package com.kfd.api.kfd_backend.settings.identity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "system_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "tagline")
    private String tagline;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "footer_copyright")
    private String footerCopyright;
}
