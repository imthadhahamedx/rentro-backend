package com.rentro.util.mapper;

import com.rentro.dto.request.vehicleCategory.VehicleCategoryRequestDto;
import com.rentro.dto.response.vehicleCategory.VehicleCategoryPublicResponseDto;
import com.rentro.dto.response.vehicleCategory.VehicleCategoryResponseDto;
import com.rentro.entity.VehicleCategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class VehicleCategoryMapper {

    public VehicleCategoryEntity toVehicleCategoryEntity(VehicleCategoryRequestDto dto){
        if(dto==null) return null;
        return VehicleCategoryEntity.builder()
                .category(dto.getCategory())
                .description(dto.getDescription())
                .iconName(dto.getIconName())
                .build();
    }

    public VehicleCategoryPublicResponseDto toVehicleCategoryPublicResponseDto(VehicleCategoryEntity vehicleCategoryEntity){
        if(vehicleCategoryEntity==null) return null;
        return VehicleCategoryPublicResponseDto.builder()
                .id(vehicleCategoryEntity.getId())
                .category(vehicleCategoryEntity.getCategory())
                .description(vehicleCategoryEntity.getDescription())
                .iconName(vehicleCategoryEntity.getIconName())
                .vehicleCount((long) vehicleCategoryEntity.getVehicles().size()).build();
    }
    public VehicleCategoryResponseDto toVehicleCategoryResponseDto(VehicleCategoryEntity vehicleCategoryEntity){
        if(vehicleCategoryEntity==null) return null;
        return VehicleCategoryResponseDto.builder()
                .id(vehicleCategoryEntity.getId())
                .category(vehicleCategoryEntity.getCategory())
                .createdAt(vehicleCategoryEntity.getCreatedAt())
                .description(vehicleCategoryEntity.getDescription())
                .iconName(vehicleCategoryEntity.getIconName())
                .vehicleCount((long) vehicleCategoryEntity.getVehicles().size()).build();
    }
}
