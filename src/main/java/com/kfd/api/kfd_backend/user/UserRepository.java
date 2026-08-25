package com.kfd.api.kfd_backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    /**
     * Counts users who currently hold the given role AND are active.
     * Used to enforce the "at least one active Super Admin must always exist" invariant.
     */
    long countByRoleNameAndIsActiveTrue(String roleName);
}
