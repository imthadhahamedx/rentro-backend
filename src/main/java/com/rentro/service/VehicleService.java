package com.rentro.service;

import com.rentro.dto.request.vehicle.VehicleRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;

import java.util.UUID;

public interface VehicleService {

    public PaginatedResponseDto search(String searchText, int page, int size);
    public PaginatedResponseDto findAllForAdmin(String searchText, int page, int size);
    public void create(VehicleRequestDto dto);
    public void deleteById(UUID id);
    public void update(UUID id, VehicleRequestDto dto);
}
