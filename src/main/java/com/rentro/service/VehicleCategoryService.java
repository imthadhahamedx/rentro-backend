package com.rentro.service;

import com.rentro.dto.request.vehicleCategory.VehicleCategoryRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;

import java.util.UUID;

public interface VehicleCategoryService {

    public PaginatedResponseDto search(String searchText, int page, int size);
    public PaginatedResponseDto findAllForAdmin(String searchText, int page, int size);
    public void create(VehicleCategoryRequestDto dto);
    public void deleteById(UUID id);
    public void update(UUID id, VehicleCategoryRequestDto dto);
}
