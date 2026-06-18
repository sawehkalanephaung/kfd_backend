package com.kfd.api.kfd_backend.config;

import com.kfd.api.kfd_backend.role.Role;
import com.kfd.api.kfd_backend.role.RoleRepository;
import com.kfd.api.kfd_backend.user.User;
import com.kfd.api.kfd_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmergencyAdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${emergency.admin.email:#{null}}")
    private String emergencyEmail;

    @Value("${emergency.admin.password:#{null}}")
    private String emergencyPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (emergencyEmail == null || emergencyEmail.isBlank() || emergencyPassword == null || emergencyPassword.isBlank()) {
            return;
        }

        log.info("Emergency Admin credentials detected. Ensuring access for {}", emergencyEmail);

        // Fetch the SUPER_ADMIN role.
        // It might be named 'SUPER_ADMIN' or 'ROLE_SUPER_ADMIN' depending on migration state.
        Optional<Role> superAdminRoleOpt = roleRepository.findByName("ROLE_SUPER_ADMIN");
        if (superAdminRoleOpt.isEmpty()) {
            superAdminRoleOpt = roleRepository.findByName("SUPER_ADMIN");
        }

        if (superAdminRoleOpt.isEmpty()) {
            log.error("Could not find SUPER_ADMIN role. Emergency seeder aborted.");
            return;
        }
        Role superAdminRole = superAdminRoleOpt.get();

        Optional<User> existingUserOpt = userRepository.findByEmail(emergencyEmail);

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            existingUser.setPassword(passwordEncoder.encode(emergencyPassword));
            existingUser.setRole(superAdminRole);
            existingUser.setIsActive(true);
            userRepository.save(existingUser);
            log.info("Emergency Admin updated successfully.");
        } else {
            User newUser = User.builder()
                    .email(emergencyEmail)
                    .password(passwordEncoder.encode(emergencyPassword))
                    .role(superAdminRole)
                    .firstName("Emergency")
                    .lastName("Admin")
                    .isActive(true)
                    .build();
            userRepository.save(newUser);
            log.info("Emergency Admin created successfully.");
        }
    }
}
