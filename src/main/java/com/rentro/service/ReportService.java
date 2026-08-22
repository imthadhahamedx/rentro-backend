package com.rentro.service;

import com.rentro.dto.response.report.BookingsReportResponseDto;
import com.rentro.dto.response.report.DamageReportSummaryResponseDto;
import com.rentro.dto.response.report.RevenueReportResponseDto;
import com.rentro.dto.response.report.VehiclesReportResponseDto;

import java.time.LocalDate;

public interface ReportService {

    /** startDate/endDate are optional (inclusive); defaults to the last 12 months when omitted. */
    RevenueReportResponseDto getRevenueReport(LocalDate startDate, LocalDate endDate);

    BookingsReportResponseDto getBookingsReport(LocalDate startDate, LocalDate endDate);

    VehiclesReportResponseDto getVehiclesReport(LocalDate startDate, LocalDate endDate);

    DamageReportSummaryResponseDto getDamageReport(LocalDate startDate, LocalDate endDate);
}
