package com.rentro.dto.response.report;

import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TopVehicleRevenueResponseDto {
    private String vehicleId;
    private String vehicleName;
    private String regNo;
    private long bookingsCount;
    private BigDecimal revenue;
}
