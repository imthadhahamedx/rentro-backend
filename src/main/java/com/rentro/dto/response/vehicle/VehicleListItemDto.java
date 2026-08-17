package com.rentro.dto.response.vehicle;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VehicleListItemDto {
    private UUID id;
    private String make;
    private String model;
    private Integer modelYear;
    private String regNo;
    private String colour;
    private String transmission;
    private String fuelType;
    private BigDecimal dailyRate;
    private String status;
    private String categoryName;
    private String primaryImageUrl;
}
