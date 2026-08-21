package com.rentro.dto.response.damage_report;

import com.rentro.dto.response.damage.DamageImageResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DamageReportDetailResponseDto {

    private UUID id;
    private LocalDateTime date;

    // Booking
    private UUID bookingId;
    private String bookingRef;
    private String bookingStatus;
    private UUID customerId;
    private String customerName;
    private String customerPhone;
    private String pickupDate;
    private String dropoffDate;

    // Damage
    private UUID damageId;
    private String damageDescription;
    private Boolean damageIsFixed;
    private String damageBy;
    private LocalDateTime damageCreatedAt;
    private LocalDateTime damageFixedAt;
    private String damageRemark;
    private List<DamageImageResponseDto> damageImages;

    // Vehicle
    private UUID vehicleId;
    private String vehicleLabel;
    private String vehicleRegNo;

    // Review
    private UUID reviewedById;
    private String reviewedByName;
    private boolean reviewed;
}
