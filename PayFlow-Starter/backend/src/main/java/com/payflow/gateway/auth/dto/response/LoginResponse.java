package com.payflow.gateway.auth.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private Long userId;

    private String fullName;

    private String email;

    private String role;

    private String accessToken;

    private String refreshToken;

    private String tokenType;

}