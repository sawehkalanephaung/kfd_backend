package com.kfd.api.kfd_backend.metric;

import java.util.List;
import java.util.UUID;

public interface GlobalMetricService {
    List<GlobalMetricResponseDTO> getAllAdmin();
    List<GlobalMetricResponseDTO> getAllPublic();
    GlobalMetricResponseDTO getById(UUID id);
    GlobalMetricResponseDTO create(GlobalMetricRequestDTO request);
    GlobalMetricResponseDTO update(UUID id, GlobalMetricRequestDTO request);
    void delete(UUID id);
}
