package com.rentro.dto.response.booking;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CustomerOptionResponseDto {
    private UUID id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String nic;
    private String drivingLicenseNo;
}
