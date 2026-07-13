package com.payflow.gateway.auth.repository;

import com.payflow.gateway.auth.entity.RefreshToken;
import com.payflow.gateway.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Find refresh token
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Find refresh token by user
     */
    Optional<RefreshToken> findByUser(User user);

    /**
     * Delete refresh token
     */
    void deleteByUser(User user);

}