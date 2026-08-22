package com.rentro.dto.response.report;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DamageReportSummaryResponseDto {
    private String startDate;
    private String endDate;

    private long totalDamages;
    private long fixedCount;
    private long unfixedCount;

    private List<LabeledCountResponseDto> byDamageBy;
    private List<MonthlyPointResponseDto> monthlyDamages;
    private List<VehicleDamageCountResponseDto> topDamagedVehicles;
}
