package com.kfd.api.kfd_backend.cms.publicationcategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PublicationCategoryRepository extends JpaRepository<PublicationCategory, UUID> {
    boolean existsBySlug(String slug);
    List<PublicationCategory> findAllByShowInPublicTrue();
}
