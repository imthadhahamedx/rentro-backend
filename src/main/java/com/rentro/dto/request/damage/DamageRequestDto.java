package com.rentro.dto.request.damage;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

/**
 * JSON body carried inside the multipart request (part name: "damage").
 * Used for both create and update.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DamageRequestDto {

    @NotNull(message = "Vehicle is required")
    private UUID vehicleId;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotBlank(message = "Damage by is required")
    @Pattern(regexp = "CUSTOMER|INTERNAL|OTHER", message = "Damage by must be one of CUSTOMER, INTERNAL, OTHER")
    private String damageBy;

    @Size(max = 2000, message = "Remark cannot exceed 2000 characters")
    private String remark;
}
