package com.rentro.dto.response.dashboard;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VehicleStatusResponseDto {

    private long total;
    private long available;
    private long rented;
    private long maintenance;
    private long inactive;

    private List<RentedVehicleResponseDto> rentedVehicles; // currently out on rent
}
