package com.rentro.dto.response.vehicleCategory;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class VehicleCategoryPublicResponseDto {

    private UUID id;
    private String category;
    private String description;
    private String iconName;
    private Long vehicleCount;
}
