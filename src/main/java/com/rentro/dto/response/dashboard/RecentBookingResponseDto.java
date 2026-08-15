package com.rentro.dto.response.dashboard;

import com.rentro.entity.BookingEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecentBookingResponseDto {

    private UUID id;
    private String bookingRef;
    private String customerName;
    private String vehicleName;   // e.g. "Toyota Aqua - CAB-1234"
    private BookingEntity.BookingStatus status;
    private LocalDate pickupDate;
    private LocalDate dropoffDate;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
