package com.kfd.api.kfd_backend.cms.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    // findAll(Pageable) is provided by the JpaRepository base interface
    boolean existsBySlug(String slug);
}
