package com.rentro.dto.response.booking;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingCustomerSummaryResponseDto {
    private UUID id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String nic;
    private String drivingLicenseNo;
    private LocalDate licenseExpiryDate;
}
