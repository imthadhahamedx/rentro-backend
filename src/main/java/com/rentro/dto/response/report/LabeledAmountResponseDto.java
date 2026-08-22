package com.rentro.dto.response.report;

import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LabeledAmountResponseDto {
    private String label;
    private BigDecimal amount;
    private long count;
}
