package com.rentro.dto.response.dashboard;

import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DashboardStatsResponseDto {

    // --- Bookings ---
    private long totalBookings;          // all-time
    private long bookingsThisMonth;
    private long activeBookings;         // status = ACTIVE (currently out)

    // --- Revenue ---
    private BigDecimal totalRevenue;     // sum of COMPLETED payments, all-time
    private BigDecimal revenueThisMonth; // sum of COMPLETED payments, current month

    // --- Vehicles ---
    private long totalVehicles;
    private long availableVehicles;

    // --- Customers ---
    private long totalCustomers;
    private long newCustomersThisMonth;

    // --- Pending payments ---
    private long pendingPaymentsCount;
    private BigDecimal pendingPaymentsAmount;

    // --- Damage reports awaiting review ---
    private long openDamageReports;
}
