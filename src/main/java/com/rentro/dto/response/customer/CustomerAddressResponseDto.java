package com.rentro.dto.response.customer;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CustomerAddressResponseDto {
    private UUID id;
    private String address;
    private String city;
    private String country;
}
