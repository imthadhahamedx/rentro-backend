package com.rentro.dto.response.damage;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DamageResponseDto {
    private UUID id;
    private String description;
    private Boolean isFixed;
    private String damageBy;
    private LocalDateTime createdAt;
    private LocalDateTime fixedAt;
    private String remark;

    private UUID vehicleId;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleRegNo;
    private String vehicleLabel;

    private UUID markedById;
    private String markedByName;

    private List<DamageImageResponseDto> images;
}
