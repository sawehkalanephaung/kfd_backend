package com.kfd.api.kfd_backend.cms.publication;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, UUID> {

    @Query("SELECT p FROM Publication p WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR p.category.name = :category) AND " +
           "(:statusEnum IS NULL OR p.status = :statusEnum)")
    Page<Publication> searchAdminPublications(@Param("search") String search, @Param("category") String category, @Param("statusEnum") PublicationStatus statusEnum, Pageable pageable);

    boolean existsBySlug(String slug);

    @Query("SELECT COUNT(p) > 0 FROM Publication p WHERE p.category.id = :categoryId")
    boolean existsByCategoryId(@Param("categoryId") UUID categoryId);

    // ── Public API queries ──────────────────────────────────────
    Page<Publication> findByStatusOrderByPublishedDateDesc(PublicationStatus status, Pageable pageable);

    Page<Publication> findByStatusAndCategorySlugOrderByPublishedDateDesc(
            PublicationStatus status, String categorySlug, Pageable pageable);

    Optional<Publication> findBySlugAndStatus(String slug, PublicationStatus status);
}
