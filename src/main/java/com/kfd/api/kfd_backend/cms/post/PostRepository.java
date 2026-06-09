package com.kfd.api.kfd_backend.cms.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    // findAll(Pageable) is provided by the JpaRepository base interface
    boolean existsBySlug(String slug);

    @Query("SELECT COUNT(p) > 0 FROM Post p JOIN p.tags t WHERE t.id = :tagId")
    boolean existsByTagId(@Param("tagId") UUID tagId);

    @Query("SELECT COUNT(p) > 0 FROM Post p WHERE p.category.id = :categoryId")
    boolean existsByCategoryId(@Param("categoryId") UUID categoryId);
}
