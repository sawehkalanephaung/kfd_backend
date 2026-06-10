package com.kfd.api.kfd_backend.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private UUID roleId;
    private String dashboardLanguage;
    private Boolean isActive;
}
