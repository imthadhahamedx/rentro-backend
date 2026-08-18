package com.rentro.dto.request.customer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CustomerUpdateRequestDto {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Pattern(
            regexp = "^[a-zA-Z\\s'-]+$",
            message = "Full name can only contain letters, spaces, hyphens, and apostrophes"
    )
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(\\+94|0)?7[0-9]{8}$",
            message = "Please provide a valid Sri Lankan phone number"
    )
    private String phoneNumber;

    @NotBlank(message = "NIC is required")
    @Size(max = 20, message = "NIC cannot exceed 20 characters")
    private String nic;

    @NotBlank(message = "Driving license number is required")
    @Size(max = 50, message = "Driving license number cannot exceed 50 characters")
    private String drivingLicenseNo;

    @NotNull(message = "License expiry date is required")
    private LocalDate licenseExpiryDate;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;

    /** Optional — only supplied when staff wants to reset the customer's password. */
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)"
    )
    private String password;

    @Valid
    private CustomerAddressRequestDto address;
}
