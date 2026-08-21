package com.rentro.dto.request.damage_report;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DamageReportRequestDto {

    @NotNull(message = "Booking ID is required")
    private UUID bookingId;

    @NotNull(message = "Damage ID is required")
    private UUID damageId;
}
