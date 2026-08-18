package com.rentro.dto.response.customer;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CustomerListItemResponseDto {
    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String nic;
    private String drivingLicenseNo;
    private LocalDate licenseExpiryDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
