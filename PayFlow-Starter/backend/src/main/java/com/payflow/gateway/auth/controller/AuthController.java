package com.payflow.gateway.auth.controller;

import com.payflow.gateway.auth.dto.request.LoginRequest;
import com.payflow.gateway.auth.dto.request.RefreshTokenRequest;
import com.payflow.gateway.auth.dto.request.RegisterRequest;
import com.payflow.gateway.auth.dto.response.LoginResponse;
import com.payflow.gateway.auth.dto.response.RegisterResponse;
import com.payflow.gateway.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new customer.
     *
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Login an existing user.
     *
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Generate a new access token using
     * a refresh token.
     *
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        LoginResponse response =
                authService.refreshToken(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Logout the current user.
     *
     * POST /api/v1/auth/logout
     *
     * The email is temporarily received as a request parameter.
     * Later, we can improve this to extract the email directly
     * from the authenticated JWT.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestParam String email) {

        authService.logout(email);

        return ResponseEntity.noContent().build();
    }
}