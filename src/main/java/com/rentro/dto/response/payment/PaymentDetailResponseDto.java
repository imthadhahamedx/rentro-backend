package com.rentro.dto.response.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetailResponseDto {

    private String id;
    private String paymentRef;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentType;
    private String status;
    private String transactionId;
    private String gatewayResponse;
    private String notes;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    // Booking info
    private String bookingId;
    private String bookingRef;
    private String customerName;
    private String customerPhoneNumber;
    private String vehicleName;
    private String vehicleRegNo;
    private BigDecimal bookingFinalAmount;
    private BigDecimal bookingTotalPaid;
    private BigDecimal bookingBalanceDue;

    // Processed by
    private String processedByName;
}
