package com.rentro.dto.response.payment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayhereHashResponseDto {

    private String merchantId;
    private String hash;
}
