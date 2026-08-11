package com.payflow.gateway.customer.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSummaryResponse {

    private Long id;

    private String fullName;

    private String email;

    private String role;

    private BigDecimal walletBalance;

    private Boolean active;
}