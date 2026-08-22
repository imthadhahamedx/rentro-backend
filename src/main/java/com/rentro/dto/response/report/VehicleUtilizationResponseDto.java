package com.rentro.dto.response.report;

import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VehicleUtilizationResponseDto {
    private String vehicleId;
    private String vehicleName;
    private String regNo;
    private String categoryName;
    private String status;
    private long bookingsCount;
    private long daysBooked;
    private BigDecimal revenue;
    /** Percentage of days in the report period the vehicle was booked, 0-100. */
    private double utilizationRate;
}
