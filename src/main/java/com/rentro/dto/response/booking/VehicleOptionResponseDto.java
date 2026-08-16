package com.rentro.dto.response.booking;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VehicleOptionResponseDto {
    private UUID id;
    private String make;
    private String model;
    private Integer modelYear;
    private String regNo;
    private String categoryName;
    private BigDecimal dailyRate;
    private String status;
}
