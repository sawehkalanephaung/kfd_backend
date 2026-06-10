package com.kfd.api.kfd_backend.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private String permissions;
}
