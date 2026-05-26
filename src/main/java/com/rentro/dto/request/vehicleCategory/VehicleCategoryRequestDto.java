package com.rentro.dto.request.vehicleCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class VehicleCategoryRequestDto {

    @NotBlank(message = "Category is required")
    @Size(max = 45, message = "Category cannot exceed 45 characters")
    private String category;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Icon name is required")
    @Size(max = 45, message = "Icon name cannot exceed 45 characters")
    private String iconName;
}
