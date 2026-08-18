package com.rentro.dto.request.customer;

import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CustomerAddressRequestDto {

    @Size(max = 1000, message = "Address cannot exceed 1000 characters")
    private String address;

    @Size(max = 45, message = "City cannot exceed 45 characters")
    private String city;

    @Size(max = 45, message = "Country cannot exceed 45 characters")
    private String country;
}
