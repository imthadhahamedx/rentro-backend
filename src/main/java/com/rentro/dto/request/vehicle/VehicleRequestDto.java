package com.rentro.dto.request.vehicle;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * JSON body carried inside the multipart request (part name: "vehicle").
 * Used for both create and update.
 */

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
    @Min(value = 1980, message = "Model year must be 1980 or later")
    @Max(value = 2100, message = "Model year is invalid")
    private Integer modelYear;

    @NotBlank(message = "Registration number is required")
    @Size(max = 45, message = "Registration number cannot exceed 45 characters")
    private String regNo;

    @Size(max = 45, message = "Colour cannot exceed 45 characters")
    private String colour;

    @NotBlank(message = "Transmission is required")
    @Pattern(regexp = "MANUAL|AUTOMATIC|TIPTRONIC", message = "Transmission must be one of MANUAL, AUTOMATIC, TIPTRONIC")
    private String transmission;

    @NotBlank(message = "Fuel type is required")
    @Pattern(regexp = "PETROL|DIESEL|HYBRID|ELECTRIC", message = "Fuel type must be one of PETROL, DIESEL, HYBRID, ELECTRIC")
    private String fuelType;

    @NotNull(message = "Seat count is required")
    @Positive(message = "Seat count must be positive")
    private Integer seatCount;

    @NotNull(message = "Door count is required")
    @Positive(message = "Door count must be positive")
    private Integer doorCount;

    @NotNull(message = "Daily rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Daily rate must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Daily rate must have at most 2 decimal places")
    private BigDecimal dailyRate;

    @Pattern(regexp = "AVAILABLE|RENTED|MAINTENANCE|INACTIVE", message = "Status must be one of AVAILABLE, RENTED, MAINTENANCE, INACTIVE")
    private String status;

    @PositiveOrZero(message = "Current mileage cannot be negative")
    private Integer currentMileageKm;

    @NotNull(message = "Vehicle category is required")
    private UUID categoryId;

    /** Optional - existing Specs to attach to this vehicle. */
    private List<UUID> specIds;
}
