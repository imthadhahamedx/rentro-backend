package com.rentro.dto.request.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentRefundRequestDto {

    @NotBlank(message = "Refund reason is required")
    private String reason;
}
