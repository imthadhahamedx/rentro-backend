package com.rentro.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PublicBookingResponseDto {
    private String      bookingRef;
    private String      vehicleName;
    private LocalDate   pickupDate;
    private LocalDate   returnDate;
    private BigDecimal  estimatedAmount;
    private String      status;
}
