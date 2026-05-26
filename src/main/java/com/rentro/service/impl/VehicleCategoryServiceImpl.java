package com.rentro.service.impl;

import com.rentro.dto.request.vehicleCategory.VehicleCategoryRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.vehicleCategory.VehicleCategoryPublicResponseDto;
import com.rentro.dto.response.vehicleCategory.VehicleCategoryResponseDto;
import com.rentro.entity.VehicleCategoryEntity;
import com.rentro.exception.EntryNotFoundException;
import com.rentro.repository.VehicleCategoryRepository;
import com.rentro.service.VehicleCategoryService;
import com.rentro.util.mapper.VehicleCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleCategoryServiceImpl implements VehicleCategoryService {

    private final VehicleCategoryRepository vehicleCategoryRepository;
    private final VehicleCategoryMapper vehicleCategoryMapper;

    @Override
    public PaginatedResponseDto search(String searchText, int page, int size) {
        return PaginatedResponseDto.<VehicleCategoryPublicResponseDto>builder()
                .count(
                   vehicleCategoryRepository.findAllCount(searchText)
                )
                .dataList(
                    vehicleCategoryRepository.findAllVehicleCategoryEntity(searchText, PageRequest.of(page,size))
                            .stream().map(e ->
                                    vehicleCategoryMapper.toVehicleCategoryPublicResponseDto(e)).toList()
                )
                .build();
    }

    @Override
    public PaginatedResponseDto findAllForAdmin(String searchText, int page, int size) {
        return PaginatedResponseDto.<VehicleCategoryResponseDto>builder()
                .count(
                        vehicleCategoryRepository.findAllCount(searchText)
                )
                .dataList(
                        vehicleCategoryRepository.findAllVehicleCategoryEntity(searchText, PageRequest.of(page,size))
                                .stream().map(e ->
                                        vehicleCategoryMapper.toVehicleCategoryResponseDto(e)).toList()
                )
                .build();
    }

    @Override
    public void create(VehicleCategoryRequestDto dto) {
        vehicleCategoryRepository.save(vehicleCategoryMapper.toVehicleCategoryEntity(dto));
    }

    @Override
    public void deleteById(UUID id) {
        vehicleCategoryRepository.deleteById(id);
    }

    @Override
    public void update(UUID id, VehicleCategoryRequestDto dto) {
        VehicleCategoryEntity vehicleCategoryEntity = vehicleCategoryRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Vehicle category not found!"));

        vehicleCategoryEntity.setCategory(dto.getCategory());
        vehicleCategoryEntity.setDescription(dto.getDescription());
        vehicleCategoryEntity.setIconName(dto.getIconName());

        vehicleCategoryRepository.save(vehicleCategoryEntity);
    }
}
