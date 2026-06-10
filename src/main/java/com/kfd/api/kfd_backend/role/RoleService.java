package com.kfd.api.kfd_backend.role;

import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    private RoleResponseDTO toDto(Role role) {
        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(role.getPermissions())
                .build();
    }

    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public RoleResponseDTO getRoleById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        return toDto(role);
    }

    @Transactional
    public RoleResponseDTO createRole(RoleRequestDTO requestDTO) {
        Role role = Role.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .permissions(requestDTO.getPermissions())
                .build();

        Role savedRole = roleRepository.save(role);
        return toDto(savedRole);
    }

    @Transactional
    public RoleResponseDTO updateRole(UUID id, RoleRequestDTO requestDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        role.setName(requestDTO.getName());
        role.setDescription(requestDTO.getDescription());
        role.setPermissions(requestDTO.getPermissions());

        Role updatedRole = roleRepository.save(role);
        return toDto(updatedRole);
    }

    @Transactional
    public void deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        roleRepository.delete(role);
    }
}
