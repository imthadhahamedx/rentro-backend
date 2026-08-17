package com.rentro.service;

import com.rentro.dto.request.vehicle.VehicleRequestDto;
import com.rentro.dto.request.vehicle.VehicleStatusUpdateRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.vehicle.VehicleResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface VehicleService {

    PaginatedResponseDto search(String searchText, UUID categoryId, String status, int page, int size);

    VehicleResponseDto findById(UUID id);

    VehicleResponseDto create(VehicleRequestDto dto, List<MultipartFile> images);

    VehicleResponseDto update(UUID id, VehicleRequestDto dto, List<MultipartFile> newImages);

    void updateStatus(UUID id, VehicleStatusUpdateRequestDto dto);

    void deleteById(UUID id);

    VehicleResponseDto addImages(UUID vehicleId, List<MultipartFile> images);

    void deleteImage(UUID vehicleId, UUID imageId);

    void setPrimaryImage(UUID vehicleId, UUID imageId);
}
