package com.kfd.api.kfd_backend.department;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DepartmentContactRepository extends JpaRepository<DepartmentContact, UUID> {
    List<DepartmentContact> findByDepartmentIdOrderByOrderIndex(UUID departmentId);
    void deleteByDepartmentId(UUID departmentId);
}
