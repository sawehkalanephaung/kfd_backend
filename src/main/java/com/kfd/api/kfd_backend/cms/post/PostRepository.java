package com.kfd.api.kfd_backend.cms.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    // findAll(Pageable) is provided by the JpaRepository base interface
    
    /**
     * The join is explicitly LEFT: the previous implicit `p.category.name` path
     * compiled to an INNER JOIN, which silently dropped every uncategorised
     * post from the unfiltered admin list.
     *
     * `:category` accepts either a category slug or its display name, matched
     * case- and whitespace-insensitively, so a stored name like
     * " General News & Articles" still resolves.
     */
    @Query("SELECT p FROM Post p LEFT JOIN p.category c WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:category IS NULL OR :category = '' " +
           "  OR LOWER(TRIM(c.slug)) = LOWER(TRIM(:category)) " +
           "  OR LOWER(TRIM(c.name)) = LOWER(TRIM(:category))) AND " +
           "(:statusEnum IS NULL OR p.status = :statusEnum)")
    Page<Post> searchAdminPosts(@Param("search") String search, @Param("category") String category, @Param("statusEnum") PostStatus statusEnum, Pageable pageable);

    boolean existsBySlug(String slug);

    @Query("SELECT COUNT(p) > 0 FROM Post p JOIN p.tags t WHERE t.id = :tagId")
    boolean existsByTagId(@Param("tagId") UUID tagId);

    @Query("SELECT COUNT(p) > 0 FROM Post p WHERE p.category.id = :categoryId")
    boolean existsByCategoryId(@Param("categoryId") UUID categoryId);

    List<Post> findByDepartmentIdOrderByPublishedAtDesc(UUID departmentId);

    // ── Public API queries ──────────────────────────────────────
    Page<Post> findByStatusOrderByPublishedAtDesc(PostStatus status, Pageable pageable);

    Page<Post> findByStatusAndCategorySlugOrderByPublishedAtDesc(
            PostStatus status, String categorySlug, Pageable pageable);

    Optional<Post> findBySlugAndStatus(String slug, PostStatus status);

    // Related posts: same category, exclude current post, limit 3
    List<Post> findTop3ByStatusAndCategoryIdAndIdNotOrderByPublishedAtDesc(
            PostStatus status, UUID categoryId, UUID excludeId);

    // Most recent published post (for featured hero)
    Optional<Post> findFirstByStatusOrderByPublishedAtDesc(PostStatus status);
}
