package com.rentro.dto.request.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VehicleStatusUpdateRequestDto {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "AVAILABLE|RENTED|MAINTENANCE|INACTIVE", message = "Status must be one of AVAILABLE, RENTED, MAINTENANCE, INACTIVE")
    private String status;
}
