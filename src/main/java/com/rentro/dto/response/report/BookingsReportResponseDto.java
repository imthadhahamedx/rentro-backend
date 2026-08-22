package com.rentro.dto.response.report;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingsReportResponseDto {
    private String startDate;
    private String endDate;

    private long totalBookings;
    private BigDecimal totalRevenue;
    private BigDecimal averageBookingValue;
    private double averageDurationDays;

    private List<LabeledCountResponseDto> byStatus;
    private List<MonthlyPointResponseDto> monthlyBookings;
    private List<LabeledCountResponseDto> byPickupLocation;
    private List<TopCustomerResponseDto> topCustomers;
}
