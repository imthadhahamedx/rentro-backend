package com.rentro.dto.response.damage;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DamageListItemResponseDto {
    private UUID id;
    private UUID vehicleId;
    private String vehicleLabel;
    private String vehicleRegNo;
    private String description;
    private Boolean isFixed;
    private String damageBy;
    private LocalDateTime createdAt;
    private String markedByName;
    private String primaryImageUrl;
    private long imageCount;
}
