package com.kfd.api.kfd_backend.department;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentContactRequestDTO {
    private String name;
    private String role;
    private String email;
    private String phone;
    private String address;
    private String websiteUrl;
    private String socialLinks;
    private String additionalDetails;
    private Integer orderIndex;
}
