package com.kfd.api.kfd_backend.settings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContactSettingsRepository extends JpaRepository<ContactSettings, UUID> {
}
