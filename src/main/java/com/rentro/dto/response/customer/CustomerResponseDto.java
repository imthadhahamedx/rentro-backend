package com.rentro.dto.response.customer;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CustomerResponseDto {
    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String nic;
    private String drivingLicenseNo;
    private LocalDate licenseExpiryDate;
    private LocalDate dateOfBirth;
    private String notes;
    private boolean isActive;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<CustomerAddressResponseDto> addresses;
    private long totalBookings;
}
