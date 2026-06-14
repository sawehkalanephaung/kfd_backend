package com.kfd.api.kfd_backend.page;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PageRepository extends JpaRepository<Page, UUID> {
    Optional<Page> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
