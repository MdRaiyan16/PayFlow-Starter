package com.payflow.gateway.auth.repository;

import com.payflow.gateway.auth.entity.User;
import com.payflow.gateway.auth.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by phone number
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Check whether email already exists
     */
    boolean existsByEmail(String email);

    /**
     * Check whether phone number already exists
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Get all users by role
     */
    List<User> findByRole(UserRole role);

    /**
     * Get enabled users
     */
    List<User> findByEnabledTrue();

    /**
     * Get disabled users
     */
    List<User> findByEnabledFalse();

}