package com.kfd.api.kfd_backend.media;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {
    List<MediaAsset> findByDepartmentId(UUID departmentId);

    // ── Public API queries ──────────────────────────────────────

    @Query("SELECT m FROM MediaAsset m WHERE " +
           "(:category = '' OR m.mediaCategory = :category) AND " +
           "(:search = '' OR LOWER(m.fileName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<MediaAsset> searchMediaPublic(@Param("category") String category, @Param("search") String search, Pageable pageable);

    @Query("SELECT new map(m.mediaCategory as category, COUNT(m) as count) FROM MediaAsset m GROUP BY m.mediaCategory")
    List<Map<String, Object>> countByCategory();
}
