package com.rentro.dto.response.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentListItemResponseDto {

    private String id;
    private String paymentRef;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentType;
    private String status;
    private String transactionId;
    private String notes;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    // Booking summary
    private String bookingId;
    private String bookingRef;
    private String customerName;
    private String vehicleName;

    // Processed by
    private String processedByName;
}
