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
    
    @Query("SELECT p FROM Post p WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR p.category.name = :category)")
    Page<Post> searchAdminPosts(@Param("search") String search, @Param("category") String category, Pageable pageable);

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
