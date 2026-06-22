package com.kfd.api.kfd_backend.department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentTimelineEventRepository extends JpaRepository<DepartmentTimelineEvent, UUID> {
    List<DepartmentTimelineEvent> findByDepartmentIdOrderByOrderIndexAsc(UUID departmentId);
}
