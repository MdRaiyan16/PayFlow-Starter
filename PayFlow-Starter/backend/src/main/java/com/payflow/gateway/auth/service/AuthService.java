package com.payflow.gateway.auth.service;

import com.payflow.gateway.auth.dto.request.LoginRequest;
import com.payflow.gateway.auth.dto.request.RefreshTokenRequest;
import com.payflow.gateway.auth.dto.request.RegisterRequest;
import com.payflow.gateway.auth.dto.response.LoginResponse;
import com.payflow.gateway.auth.dto.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

    void logout(String email);

}