package com.payflow.gateway.customer.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id;

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String role;

    private BigDecimal walletBalance;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}