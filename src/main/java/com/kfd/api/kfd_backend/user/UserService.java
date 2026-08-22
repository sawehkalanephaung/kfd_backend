package com.kfd.api.kfd_backend.user;

import com.kfd.api.kfd_backend.role.Role;
import com.kfd.api.kfd_backend.role.RoleRepository;
import com.kfd.api.kfd_backend.role.RoleResponseDTO;

import com.kfd.api.kfd_backend.global.exception.LastSuperAdminException;
import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    /** Role name seeded by V6__update_users_table_for_auth.sql. */
    private static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private boolean isActiveSuperAdmin(User user) {
        return user.getRole() != null
                && ROLE_SUPER_ADMIN.equals(user.getRole().getName())
                && Boolean.TRUE.equals(user.getIsActive());
    }

    /**
     * Refuses an operation that would leave zero active Super Admins.
     *
     * Must be called BEFORE any change is applied to the entity, otherwise the
     * count below would already include the modification we are trying to validate.
     */
    private void assertNotLastSuperAdmin(User user, String action) {
        if (!isActiveSuperAdmin(user)) return;

        if (userRepository.countByRoleNameAndIsActiveTrue(ROLE_SUPER_ADMIN) <= 1) {
            throw new LastSuperAdminException(
                    "Cannot " + action + " the only remaining active Super Admin. "
                            + "Create or activate another Super Admin first.");
        }
    }

    private RoleResponseDTO toRoleDto(Role role) {
        if (role == null) return null;
        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(role.getPermissions())
                .build();
    }

    private UserResponseDTO toDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(toRoleDto(user.getRole()))
                .dashboardLanguage(user.getDashboardLanguage())
                .isActive(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDto);
    }

    public UserResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return toDto(user);
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        Role role = roleRepository.findById(requestDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", requestDTO.getRoleId()));

        User user = User.builder()
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .role(role)
                .dashboardLanguage(requestDTO.getDashboardLanguage())
                .isActive(requestDTO.getIsActive() != null ? requestDTO.getIsActive() : true)
                .build();

        User savedUser = userRepository.save(user);
        return toDto(savedUser);
    }

    @Transactional
    public UserResponseDTO updateUser(UUID id, UserRequestDTO requestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        Role role = roleRepository.findById(requestDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", requestDTO.getRoleId()));

        // Guard the invariant before mutating: this update must not remove the last Super Admin,
        // either by moving them off the role or by deactivating them.
        boolean losesSuperAdmin = !ROLE_SUPER_ADMIN.equals(role.getName())
                || Boolean.FALSE.equals(requestDTO.getIsActive());
        if (losesSuperAdmin) {
            assertNotLastSuperAdmin(user, "demote or deactivate");
        }

        user.setEmail(requestDTO.getEmail());
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        user.setRole(role);
        user.setDashboardLanguage(requestDTO.getDashboardLanguage());
        if (requestDTO.getIsActive() != null) {
            user.setIsActive(requestDTO.getIsActive());
        }

        User updatedUser = userRepository.save(user);
        return toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        assertNotLastSuperAdmin(user, "delete");

        userRepository.delete(user);
    }
}
