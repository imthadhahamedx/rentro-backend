package com.rentro.dto.request.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingCompleteRequestDto {

    @NotNull(message = "Actual return date is required")
    private LocalDate actualReturnDate;

    @PositiveOrZero(message = "Extra charges cannot be negative")
    private BigDecimal extraCharges;

    @Size(max = 2000, message = "Extra charges note cannot exceed 2000 characters")
    private String extraChargesNote;

    private Integer currentMileageKm;
}
