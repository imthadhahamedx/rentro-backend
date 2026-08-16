package com.rentro.dto.response.booking;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingListItemResponseDto {
    private UUID id;
    private String bookingRef;
    private String status;
    private String customerName;
    private String customerPhone;
    private String vehicleName;
    private String regNo;
    private String pickupLocationName;
    private String dropoffLocationName;
    private LocalDate pickupDate;
    private LocalDate dropoffDate;
    private BigDecimal totalAmount;
    private BigDecimal finalAmount;
    private LocalDateTime createdAt;
}
