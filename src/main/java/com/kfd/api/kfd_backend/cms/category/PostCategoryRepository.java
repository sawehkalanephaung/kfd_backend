package com.kfd.api.kfd_backend.cms.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, UUID> {
    boolean existsBySlug(String slug);
}
