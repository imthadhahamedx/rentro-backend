package com.rentro.dto.response.dashboard;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RentedVehicleResponseDto {

    private UUID vehicleId;
    private String vehicleName;   // "Toyota Aqua"
    private String regNo;
    private String bookingRef;
    private String customerName;
    private LocalDate dropoffDate;
}
