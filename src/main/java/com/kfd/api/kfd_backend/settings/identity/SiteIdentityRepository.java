package com.kfd.api.kfd_backend.settings.identity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SiteIdentityRepository extends JpaRepository<SiteIdentity, UUID> {
}
