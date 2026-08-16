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
public class BookingDetailResponseDto {
    private UUID id;
    private String bookingRef;
    private String status;

    private LocalDate pickupDate;
    private LocalDate dropoffDate;
    private LocalDate actualReturnDate;
    private Integer totalDays;

    private BigDecimal dailyRate;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal extraCharges;
    private String extraChargesNote;
    private BigDecimal finalAmount;

    private BigDecimal totalPaid;
    private BigDecimal balanceDue;

    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String createdByName;
    private String approvedByName;

    private BookingCustomerSummaryResponseDto customer;
    private BookingVehicleSummaryResponseDto vehicle;
    private BookingLocationSummaryResponseDto pickupLocation;
    private BookingLocationSummaryResponseDto dropoffLocation;

    private BookingExtensionResponseDto extension;

    private int damageReportsCount;
    private int paymentsCount;
}
