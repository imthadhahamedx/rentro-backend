package com.rentro.dto.response.report;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VehiclesReportResponseDto {
    private String startDate;
    private String endDate;

    private long totalVehicles;

    private List<LabeledCountResponseDto> byStatus;
    private List<LabeledCountResponseDto> byCategory;
    private List<VehicleUtilizationResponseDto> utilization;
}
