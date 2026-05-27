package com.rentro.dto.request.vehicle;

import com.rentro.entity.VehicleCategoryEntity;
import com.rentro.entity.VehicleEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class VehicleRequestDto {

    @NotBlank(message = "Make is required")
    @Size(max = 45, message = "Make cannot exceed 45 characters")
    private String make;

    @NotBlank(message = "Model is required")
    @Size(max = 45, message = "Model cannot exceed 45 characters")
    private String model;

    @NotNull(message = "Model year is required")
    private Integer modelYear;

    @NotBlank(message = "Registration number is required")
    @Size(max = 45, message = "Registration number cannot exceed 45 characters")
    private String regNo;

    @NotBlank(message = "Colour is required")
    @Size(max = 45, message = "Colour cannot exceed 45 characters")
    private String colour;

    @NotNull(message = "Transmission is required")
    private VehicleEntity.Transmission transmission;

    @NotNull(message = "Fuel type is required")
    private VehicleEntity.FuelType fuelType;

    @NotNull(message = "Seat count is required")
    private Integer seatCount;

    @NotNull(message = "Door count is required")
    private Integer doorCount;

    @NotNull(message = "Daily rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Daily rate must be positive")
    private BigDecimal dailyRate;

    @NotNull(message = "Status is required")
    private VehicleEntity.Status status;

    @NotNull(message = "Current mileage is required")
    private Integer currentMileageKm;

    @NotNull(message = "Vehicle category ID is required")
    private VehicleCategoryEntity vehicleCategory;

}
