package com.rentro.dto.request.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingExtensionRequestDto {

    @NotNull(message = "New dropoff date is required")
    private LocalDate newDropoffDate;

    @Size(max = 1000, message = "Reason cannot exceed 1000 characters")
    private String reason;
}
