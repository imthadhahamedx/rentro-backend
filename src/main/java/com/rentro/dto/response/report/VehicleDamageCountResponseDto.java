package com.rentro.dto.response.report;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VehicleDamageCountResponseDto {
    private String vehicleId;
    private String vehicleName;
    private String regNo;
    private long damageCount;
    private long unfixedCount;
}
