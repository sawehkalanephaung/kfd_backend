package com.kfd.api.kfd_backend.settings.footer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FooterLinkRepository extends JpaRepository<FooterLink, UUID> {
}
