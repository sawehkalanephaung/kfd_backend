package com.kfd.api.kfd_backend.settings.social;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SocialMediaLinkRepository extends JpaRepository<SocialMediaLink, UUID> {
    List<SocialMediaLink> findAllByOrderByDisplayOrderAsc();
    List<SocialMediaLink> findByIsActiveTrueOrderByDisplayOrderAsc();
}
