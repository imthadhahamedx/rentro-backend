package com.rentro.dto.request.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PaymentCreateRequestDto {

    @NotNull(message = "Booking ID is required")
    private UUID bookingId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    /** CASH | CREDIT_CARD | DEBIT_CARD | BANK_TRANSFER | ONLINE */
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    /** DEPOSIT | RENTAL_FEE | EXTRA_CHARGE */
    @NotBlank(message = "Payment type is required")
    private String paymentType;

    /** Optional: filled by PayHere notify webhook or manual entry */
    private String transactionId;

    private String notes;
}
