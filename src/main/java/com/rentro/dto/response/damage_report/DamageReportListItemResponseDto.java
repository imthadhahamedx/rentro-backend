package com.rentro.dto.response.damage_report;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DamageReportListItemResponseDto {

    private UUID id;
    private LocalDateTime date;

    // Booking summary
    private UUID bookingId;
    private String bookingRef;
    private String customerName;

    // Damage summary
    private UUID damageId;
    private String vehicleLabel;
    private String vehicleRegNo;
    private String damageDescription;
    private Boolean damageIsFixed;
    private String damageBy;
    private String primaryImageUrl;

    // Review state
    private UUID reviewedById;
    private String reviewedByName;
    private boolean reviewed;
}
