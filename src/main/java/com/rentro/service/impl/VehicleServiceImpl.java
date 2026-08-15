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

    @Override
    public PaginatedResponseDto search(String searchText, int page, int size) {
        return null;
    }

    @Override
    public PaginatedResponseDto findAllForAdmin(String searchText, int page, int size) {
        return null;
    }

    @Override
    public void create(VehicleRequestDto dto) {

    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public void update(UUID id, VehicleRequestDto dto) {

    }
}
