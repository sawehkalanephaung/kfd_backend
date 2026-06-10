package com.kfd.api.kfd_backend.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequestDTO {
    private String name;
    private String description;
    private String permissions; // Assuming JSON string for now
}
