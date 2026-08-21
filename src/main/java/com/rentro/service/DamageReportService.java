package com.rentro.service;

import com.rentro.dto.request.damage_report.DamageReportRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.damage_report.DamageReportDetailResponseDto;

import java.util.UUID;

public interface DamageReportService {

    PaginatedResponseDto search(String searchText, String reviewed, int page, int size);

    DamageReportDetailResponseDto findById(UUID id);

    DamageReportDetailResponseDto create(DamageReportRequestDto dto);

    void markAsReviewed(UUID id);

    void deleteById(UUID id);
}
