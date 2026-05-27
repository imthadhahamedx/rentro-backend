package com.rentro.util.mapper;

import com.rentro.dto.request.vehicle.VehicleRequestDto;
import com.rentro.dto.response.vehicle.VehiclePublicResponseDto;
import com.rentro.dto.response.vehicle.VehicleResponseDto;
import com.rentro.entity.VehicleEntity;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public VehicleEntity toVehicleEntity(VehicleRequestDto dto){
        if(dto == null) return null;

        return VehicleEntity.builder()
                .make(dto.getMake())
                .model(dto.getModel())
                .modelYear(dto.getModelYear())
                .regNo(dto.getRegNo())
                .colour(dto.getColour())
                .transmission(dto.getTransmission())
                .fuelType(dto.getFuelType())
                .seatCount(dto.getSeatCount())
                .doorCount(dto.getDoorCount())
                .dailyRate(dto.getDailyRate())
                .status(dto.getStatus())
                .currentMileageKm(dto.getCurrentMileageKm())
                .vehicleCategory(dto.getVehicleCategory())
                .build();
    }

    public VehiclePublicResponseDto toVehiclePublicResponseDto(VehicleEntity vehicleEntity){
        if(vehicleEntity == null) return null;

        return VehiclePublicResponseDto.builder()
                .id(vehicleEntity.getId())
                .make(vehicleEntity.getMake())
                .model(vehicleEntity.getModel())
                .modelYear(vehicleEntity.getModelYear())
                .regNo(vehicleEntity.getRegNo())
                .colour(vehicleEntity.getColour())
                .transmission(vehicleEntity.getTransmission())
                .fuelType(vehicleEntity.getFuelType())
                .seatCount(vehicleEntity.getSeatCount())
                .doorCount(vehicleEntity.getDoorCount())
                .dailyRate(vehicleEntity.getDailyRate())
                .status(vehicleEntity.getStatus())
                .currentMileageKm(vehicleEntity.getCurrentMileageKm())
                .build();
    }

    public VehicleResponseDto toVehicleResponseDto(VehicleEntity vehicleEntity){
        if(vehicleEntity == null) return null;

        return VehicleResponseDto.builder()
                .id(vehicleEntity.getId())
                .make(vehicleEntity.getMake())
                .model(vehicleEntity.getModel())
                .modelYear(vehicleEntity.getModelYear())
                .regNo(vehicleEntity.getRegNo())
                .colour(vehicleEntity.getColour())
                .transmission(vehicleEntity.getTransmission())
                .fuelType(vehicleEntity.getFuelType())
                .seatCount(vehicleEntity.getSeatCount())
                .doorCount(vehicleEntity.getDoorCount())
                .dailyRate(vehicleEntity.getDailyRate())
                .createdAt(vehicleEntity.getCreatedAt())
                .updatedAt(vehicleEntity.getUpdatedAt())
                .status(vehicleEntity.getStatus())
                .currentMileageKm(vehicleEntity.getCurrentMileageKm())
                .vehicleCategory(vehicleEntity.getVehicleCategory())
                .build();
    }
}
