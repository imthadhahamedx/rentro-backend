package com.rentro.service;

import com.rentro.dto.request.location.LocationRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.location.LocationResponseDto;

import java.util.UUID;

public interface LocationService {

    PaginatedResponseDto findAllForAdmin(String searchText, Boolean isActive, int page, int size);

    LocationResponseDto findById(UUID id);

    LocationResponseDto create(LocationRequestDto dto);

    LocationResponseDto update(UUID id, LocationRequestDto dto);

    void updateStatus(UUID id, boolean isActive);

    void deleteById(UUID id);
}
