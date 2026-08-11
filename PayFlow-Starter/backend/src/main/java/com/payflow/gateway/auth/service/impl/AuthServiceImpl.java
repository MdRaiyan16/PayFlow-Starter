package com.payflow.gateway.auth.service.impl;

import com.payflow.gateway.auth.dto.request.LoginRequest;
import com.payflow.gateway.auth.dto.request.RefreshTokenRequest;
import com.payflow.gateway.auth.dto.request.RegisterRequest;
import com.payflow.gateway.auth.dto.response.LoginResponse;
import com.payflow.gateway.auth.dto.response.RegisterResponse;
import com.payflow.gateway.auth.entity.RefreshToken;
import com.payflow.gateway.auth.entity.User;
import com.payflow.gateway.auth.entity.UserRole;
import com.payflow.gateway.auth.repository.RefreshTokenRepository;
import com.payflow.gateway.auth.repository.UserRepository;
import com.payflow.gateway.auth.service.AuthService;
import com.payflow.gateway.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Register a new user.
     */
    @Override
    public RegisterResponse register(RegisterRequest request) {

        /*
         * Check whether the email is already registered.
         */
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        /*
         * Check whether the phone number is already registered.
         */
        if (request.getPhoneNumber() != null
                && !request.getPhoneNumber().isBlank()
                && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {

            throw new IllegalArgumentException(
                    "Phone number is already registered"
            );
        }

        /*
         * Create User entity.
         */
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        /*
         * Never save a plain-text password.
         */
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        /*
         * For normal registration, create the user as CUSTOMER.
         *
         * We don't allow a client to register itself as ADMIN.
         */
        user.setRole(UserRole.ROLE_CUSTOMER);

        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);

        /*
         * Save user in MySQL.
         */
        User savedUser = userRepository.save(user);

        /*
         * Build registration response.
         */
        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .role(savedUser.getRole().name())
                .message("User registered successfully")
                .build();
    }

    /**
     * Login existing user.
     */
    @Override
    public LoginResponse login(LoginRequest request) {

        /*
         * Ask Spring Security to authenticate the user.
         *
         * AuthenticationManager will internally use
         * CustomUserDetailsService and PasswordEncoder.
         */
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        /*
         * Get the user from database.
         */
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        /*
         * Add useful information to the JWT.
         */
        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());

        /*
         * Generate access token.
         */
        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                claims
        );

        /*
         * Generate refresh token.
         */
        String refreshTokenValue = jwtService.generateRefreshToken(
                user.getEmail()
        );

        /*
         * Remove old refresh token for this user.
         */
        refreshTokenRepository
                .findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        /*
         * Calculate refresh token expiry.
         *
         * JwtService stores expiration as milliseconds.
         */
        LocalDateTime refreshTokenExpiry =
                LocalDateTime.now()
                        .plusNanos(getRefreshTokenExpiration() * 1_000_000);
        /*
         * Save refresh token.
         */
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .expiryDate(refreshTokenExpiry)
                .revoked(false)
                .expired(false)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);

        /*
         * Return login response.
         */
        return LoginResponse.builder()
                .userId(user.getId())
                .fullName(
                        user.getFirstName() + " " + user.getLastName()
                )
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .build();
    }

    /**
     * Generate a new access token using a refresh token.
     */
    @Override
    public LoginResponse refreshToken(
            RefreshTokenRequest request
    ) {

        /*
         * Find refresh token in database.
         */
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid refresh token"
                        )
                );

        /*
         * Check whether token has been revoked.
         */
        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new IllegalArgumentException(
                    "Refresh token has been revoked"
            );
        }

        /*
         * Check whether token has been marked expired.
         */
        if (Boolean.TRUE.equals(refreshToken.getExpired())) {
            throw new IllegalArgumentException(
                    "Refresh token has expired"
            );
        }

        /*
         * Check expiry date.
         */
        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            refreshToken.setExpired(true);
            refreshTokenRepository.save(refreshToken);

            throw new IllegalArgumentException(
                    "Refresh token has expired"
            );
        }

        User user = refreshToken.getUser();

        /*
         * Create JWT claims.
         */
        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());

        /*
         * Generate new access token.
         */
        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                claims
        );

        /*
         * Return existing refresh token together with
         * the new access token.
         */
        return LoginResponse.builder()
                .userId(user.getId())
                .fullName(
                        user.getFirstName() + " " + user.getLastName()
                )
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }

    /**
     * Logout user.
     *
     * AuthService currently defines:
     *
     * void logout(String email);
     */
    @Override
    public void logout(String email) {

        /*
         * Find user by email.
         */
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        /*
         * Find and delete the user's refresh token.
         */
        refreshTokenRepository
                .findByUser(user)
                .ifPresent(refreshTokenRepository::delete);
    }

    /**
     * Get refresh-token expiration from application properties.
     *
     * We need the same value that JwtService uses.
     */
    private long getRefreshTokenExpiration() {

        return refreshTokenExpiration;
    }

    /*
     * This value is injected from:
     *
     * jwt.refresh-token-expiration
     */
    @org.springframework.beans.factory.annotation.Value(
            "${jwt.refresh-token-expiration}"
    )
    private long refreshTokenExpiration;
}