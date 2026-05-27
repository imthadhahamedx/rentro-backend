package com.rentro.dto.response.vehicle;

import com.rentro.entity.VehicleCategoryEntity;
import com.rentro.entity.VehicleEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class VehicleResponseDto {

    private UUID id;

    private String make;

    private String model;

    private Integer modelYear;

    private String regNo;

    private String colour;

    private VehicleEntity.Transmission transmission;

    private VehicleEntity.FuelType fuelType;

    private Integer seatCount;

    private Integer doorCount;

    private BigDecimal dailyRate;

    private VehicleEntity.Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer currentMileageKm;

    private VehicleCategoryEntity vehicleCategory;

    private Long vehicleCount;
}
