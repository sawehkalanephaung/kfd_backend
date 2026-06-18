package com.kfd.api.kfd_backend.metric;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GlobalMetricRepository extends JpaRepository<GlobalMetric, UUID> {
    List<GlobalMetric> findAllByIsActiveTrueOrderByDisplayOrderAsc();
}
