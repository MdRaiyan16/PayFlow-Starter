package com.payflow.gateway.auth.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String role;

    private String message;

}