package com.kfd.api.kfd_backend.settings.footer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FooterLinkSectionRepository extends JpaRepository<FooterLinkSection, UUID> {
    List<FooterLinkSection> findByIsActiveTrueOrderByDisplayOrderAsc();
    List<FooterLinkSection> findAllByOrderByDisplayOrderAsc();
}
