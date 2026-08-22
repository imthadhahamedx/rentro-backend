package com.rentro.dto.response.report;

import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TopCustomerResponseDto {
    private String customerId;
    private String customerName;
    private long bookingsCount;
    private BigDecimal totalSpent;
}
