package com.rentro.dto.request.location;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LocationRequestDto {

    @NotBlank(message = "Location name is required")
    @Size(max = 45, message = "Location name cannot exceed 45 characters")
    private String locationName;

    @Size(max = 5000, message = "Address cannot exceed 5000 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 45, message = "City cannot exceed 45 characters")
    private String city;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    private Boolean isActive;
}
