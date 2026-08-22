package com.rentro.dto.response.report;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RevenueReportResponseDto {
    private String startDate;
    private String endDate;

    private BigDecimal totalRevenue;
    private long totalPayments;
    private BigDecimal averagePaymentAmount;

    private List<MonthlyPointResponseDto> monthlyRevenue;
    private List<LabeledAmountResponseDto> byPaymentMethod;
    private List<LabeledAmountResponseDto> byPaymentType;
    private List<TopVehicleRevenueResponseDto> topVehicles;
}
