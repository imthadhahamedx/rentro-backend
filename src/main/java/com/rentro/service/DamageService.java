package com.rentro.service;

import com.rentro.dto.request.damage.DamageFixedUpdateRequestDto;
import com.rentro.dto.request.damage.DamageRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.damage.DamageResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface DamageService {

    PaginatedResponseDto search(String searchText, String status, String damageBy, UUID vehicleId, int page, int size);

    DamageResponseDto findById(UUID id);

    DamageResponseDto create(DamageRequestDto dto, List<MultipartFile> images);

    DamageResponseDto update(UUID id, DamageRequestDto dto, List<MultipartFile> newImages);

    void updateFixedStatus(UUID id, DamageFixedUpdateRequestDto dto);

    void deleteById(UUID id);

    DamageResponseDto addImages(UUID damageId, List<MultipartFile> images);

    void deleteImage(UUID damageId, UUID imageId);
}
