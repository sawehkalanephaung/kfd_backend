package com.kfd.api.kfd_backend.metric;

import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GlobalMetricServiceImpl implements GlobalMetricService {

    private final GlobalMetricRepository globalMetricRepository;

    private GlobalMetricResponseDTO toDto(GlobalMetric metric) {
        return GlobalMetricResponseDTO.builder()
                .id(metric.getId())
                .title(metric.getTitle())
                .metricValue(metric.getMetricValue())
                .icon(metric.getIcon())
                .displayOrder(metric.getDisplayOrder())
                .isActive(metric.getIsActive())
                .createdAt(metric.getCreatedAt())
                .updatedAt(metric.getUpdatedAt())
                .build();
    }

    @Override
    public List<GlobalMetricResponseDTO> getAllAdmin() {
        return globalMetricRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GlobalMetricResponseDTO> getAllPublic() {
        return globalMetricRepository.findAllByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public GlobalMetricResponseDTO getById(UUID id) {
        GlobalMetric metric = globalMetricRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GlobalMetric", "id", id));
        return toDto(metric);
    }

    @Override
    @Transactional
    public GlobalMetricResponseDTO create(GlobalMetricRequestDTO request) {
        GlobalMetric metric = GlobalMetric.builder()
                .title(request.getTitle())
                .metricValue(request.getMetricValue())
                .icon(request.getIcon())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        
        return toDto(globalMetricRepository.save(metric));
    }

    @Override
    @Transactional
    public GlobalMetricResponseDTO update(UUID id, GlobalMetricRequestDTO request) {
        GlobalMetric metric = globalMetricRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GlobalMetric", "id", id));
        
        metric.setTitle(request.getTitle());
        metric.setMetricValue(request.getMetricValue());
        metric.setIcon(request.getIcon());
        
        if (request.getDisplayOrder() != null) {
            metric.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getIsActive() != null) {
            metric.setIsActive(request.getIsActive());
        }
        
        return toDto(globalMetricRepository.save(metric));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        GlobalMetric metric = globalMetricRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GlobalMetric", "id", id));
        globalMetricRepository.delete(metric);
    }
}
