package com.rentro.service.impl;

import com.rentro.dto.request.vehicle.VehicleRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.vehicle.VehiclePublicResponseDto;
import com.rentro.dto.response.vehicle.VehicleResponseDto;
import com.rentro.entity.VehicleEntity;
import com.rentro.exception.EntryNotFoundException;
import com.rentro.repository.VehicleRepository;
import com.rentro.service.VehicleService;
import com.rentro.util.mapper.VehicleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    @Override
    public PaginatedResponseDto search(String searchText, int page, int size) {
        return PaginatedResponseDto.<VehiclePublicResponseDto>builder()
                .count(
                        vehicleRepository.findAllCount(searchText)
                )
                .dataList(
                        vehicleRepository.findAllVehicleEntity(searchText, PageRequest.of(page,size))
                                .stream().map((e) -> vehicleMapper.toVehiclePublicResponseDto(e)).toList()
                )
                .build();
    }

    @Override
    public PaginatedResponseDto findAllForAdmin(String searchText, int page, int size) {
        return PaginatedResponseDto.<VehicleResponseDto>builder()
                .count(
                        vehicleRepository.findAllCount(searchText)
                )
                .dataList(
                        vehicleRepository.findAllVehicleEntity(searchText, PageRequest.of(page,size))
                                .stream().map((e) -> vehicleMapper.toVehicleResponseDto(e)).toList()
                )
                .build();
    }

    @Override
    public void create(VehicleRequestDto dto) {
        vehicleRepository.save(vehicleMapper.toVehicleEntity(dto));
    }

    @Override
    public void deleteById(UUID id) {
        vehicleRepository.deleteById(id);
    }

    @Override
    public void update(UUID id, VehicleRequestDto dto) {
        VehicleEntity vehicleEntity = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Vehicle not found"));

        vehicleEntity.setMake(dto.getMake());
        vehicleEntity.setModel(dto.getModel());
        vehicleEntity.setModelYear(dto.getModelYear());
        vehicleEntity.setRegNo(dto.getRegNo());
        vehicleEntity.setColour(dto.getColour());
        vehicleEntity.setTransmission(dto.getTransmission());
        vehicleEntity.setFuelType(dto.getFuelType());
        vehicleEntity.setSeatCount(dto.getSeatCount());
        vehicleEntity.setDoorCount(dto.getDoorCount());
        vehicleEntity.setDailyRate(dto.getDailyRate());
        vehicleEntity.setStatus(dto.getStatus());
        vehicleEntity.setCurrentMileageKm(dto.getCurrentMileageKm());
        vehicleEntity.setVehicleCategory(dto.getVehicleCategory());

        vehicleRepository.save(vehicleEntity);
    }
}
