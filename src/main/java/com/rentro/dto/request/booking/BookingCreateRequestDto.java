package com.rentro.dto.request.booking;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingCreateRequestDto {

    @NotNull(message = "Customer is required")
    private UUID customerId;

    @NotNull(message = "Vehicle is required")
    private UUID vehicleId;

    @NotNull(message = "Pickup location is required")
    private UUID pickupLocationId;

    @NotNull(message = "Dropoff location is required")
    private UUID dropoffLocationId;

    @NotNull(message = "Pickup date is required")
    @FutureOrPresent(message = "Pickup date cannot be in the past")
    private LocalDate pickupDate;

    @NotNull(message = "Dropoff date is required")
    private LocalDate dropoffDate;

    @PositiveOrZero(message = "Discount cannot be negative")
    private BigDecimal discountAmount;

    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;
}
