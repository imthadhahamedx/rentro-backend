package com.rentro.dto.response.report;

import lombok.*;

import java.math.BigDecimal;

/** One point on a month-by-month trend line (used for revenue, bookings, and damages). */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MonthlyPointResponseDto {
    private String month; // e.g. "Jan 2026"
    private long count;
    private BigDecimal amount;
}
