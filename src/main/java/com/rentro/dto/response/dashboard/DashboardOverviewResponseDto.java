package com.rentro.dto.response.dashboard;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DashboardOverviewResponseDto {

    private DashboardStatsResponseDto stats;
    private List<RecentBookingResponseDto> recentBookings;
    private VehicleStatusResponseDto vehicleStatus;
}
