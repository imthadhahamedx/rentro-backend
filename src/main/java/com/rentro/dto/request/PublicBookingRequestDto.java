package com.rentro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PublicBookingRequestDto {

    @NotNull(message = "Vehicle ID is required")
    private UUID vehicleId;

    @NotBlank(message = "Customer name is required")
    @Size(max = 150, message = "Name must be at most 150 characters")
    private String customerName;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[\\d\\s\\+\\-]{7,20}$", message = "Invalid contact number")
    private String contactNumber;

    @NotBlank(message = "Pickup location is required")
    private String pickupLocation;

    @NotNull(message = "Pickup date is required")
    private LocalDate pickupDate;

    @NotNull(message = "Return date is required")
    private LocalDate returnDate;
}
