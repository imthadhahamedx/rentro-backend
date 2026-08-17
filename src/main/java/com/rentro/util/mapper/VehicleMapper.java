package com.rentro.util.mapper;

import com.rentro.dto.request.vehicle.VehicleRequestDto;
import com.rentro.dto.response.vehicle.VehicleImageResponseDto;
import com.rentro.dto.response.vehicle.VehicleListItemDto;
import com.rentro.dto.response.vehicle.VehicleResponseDto;
import com.rentro.entity.SpecsEntity;
import com.rentro.entity.VehicleEntity;
import com.rentro.entity.VehicleImageEntity;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
                .transmission(VehicleEntity.Transmission.valueOf(dto.getTransmission()))
                .fuelType(VehicleEntity.FuelType.valueOf(dto.getFuelType()))
                .seatCount(dto.getSeatCount())
                .doorCount(dto.getDoorCount())
                .dailyRate(dto.getDailyRate())
                .status(dto.getStatus() != null ? VehicleEntity.Status.valueOf(dto.getStatus()) : VehicleEntity.Status.AVAILABLE)
                .currentMileageKm(dto.getCurrentMileageKm())
                .build();
    }

    /** Applies request fields onto an existing managed entity (used for update). */
    public void updateVehicleFromDto(VehicleEntity vehicleEntity, VehicleRequestDto dto) {
        vehicleEntity.setMake(dto.getMake());
        vehicleEntity.setModel(dto.getModel());
        vehicleEntity.setModelYear(dto.getModelYear());
        vehicleEntity.setRegNo(dto.getRegNo());
        vehicleEntity.setColour(dto.getColour());
        vehicleEntity.setTransmission(VehicleEntity.Transmission.valueOf(dto.getTransmission()));
        vehicleEntity.setFuelType(VehicleEntity.FuelType.valueOf(dto.getFuelType()));
        vehicleEntity.setSeatCount(dto.getSeatCount());
        vehicleEntity.setDoorCount(dto.getDoorCount());
        vehicleEntity.setDailyRate(dto.getDailyRate());
        if (dto.getStatus() != null) {
            vehicleEntity.setStatus(VehicleEntity.Status.valueOf(dto.getStatus()));
        }
        vehicleEntity.setCurrentMileageKm(dto.getCurrentMileageKm());
    }

    public VehicleListItemDto toVehicleListItemDTO(VehicleEntity vehicleEntity) {
        if (vehicleEntity == null) return null;
        return VehicleListItemDto.builder()
                .id(vehicleEntity.getId())
                .make(vehicleEntity.getMake())
                .model(vehicleEntity.getModel())
                .modelYear(vehicleEntity.getModelYear())
                .regNo(vehicleEntity.getRegNo())
                .colour(vehicleEntity.getColour())
                .transmission(vehicleEntity.getTransmission() != null ? vehicleEntity.getTransmission().name() : null)
                .fuelType(vehicleEntity.getFuelType() != null ? vehicleEntity.getFuelType().name() : null)
                .dailyRate(vehicleEntity.getDailyRate())
                .status(vehicleEntity.getStatus() != null ? vehicleEntity.getStatus().name() : null)
                .categoryName(vehicleEntity.getVehicleCategory() != null ? vehicleEntity.getVehicleCategory().getCategory() : null)
                .primaryImageUrl(resolvePrimaryImageUrl(vehicleEntity.getVehicleImages()))
                .build();
    }

    public VehicleResponseDto toVehicleResponseDto(VehicleEntity vehicleEntity) {
        if (vehicleEntity == null) return null;
        return VehicleResponseDto.builder()
                .id(vehicleEntity.getId())
                .make(vehicleEntity.getMake())
                .model(vehicleEntity.getModel())
                .modelYear(vehicleEntity.getModelYear())
                .regNo(vehicleEntity.getRegNo())
                .colour(vehicleEntity.getColour())
                .transmission(vehicleEntity.getTransmission() != null ? vehicleEntity.getTransmission().name() : null)
                .fuelType(vehicleEntity.getFuelType() != null ? vehicleEntity.getFuelType().name() : null)
                .seatCount(vehicleEntity.getSeatCount())
                .doorCount(vehicleEntity.getDoorCount())
                .dailyRate(vehicleEntity.getDailyRate())
                .status(vehicleEntity.getStatus() != null ? vehicleEntity.getStatus().name() : null)
                .currentMileageKm(vehicleEntity.getCurrentMileageKm())
                .createdAt(vehicleEntity.getCreatedAt())
                .updatedAt(vehicleEntity.getUpdatedAt())
                .categoryId(vehicleEntity.getVehicleCategory() != null ? vehicleEntity.getVehicleCategory().getId() : null)
                .categoryName(vehicleEntity.getVehicleCategory() != null ? vehicleEntity.getVehicleCategory().getCategory() : null)
                .specifications(
                        vehicleEntity.getSpecs() == null ? List.of() :
                                vehicleEntity.getSpecs().stream().map(SpecsEntity::getSpecification).filter(Objects::nonNull).toList()
                )
                .images(
                        vehicleEntity.getVehicleImages() == null ? List.of() :
                                vehicleEntity.getVehicleImages().stream().map(this::toVehicleImageResponseDTO).toList()
                )
                .build();
    }

    public VehicleImageResponseDto toVehicleImageResponseDTO(VehicleImageEntity image) {
        if (image == null) return null;
        return VehicleImageResponseDto.builder()
                .id(image.getId())
                .fileName(image.getFileName())
                .url(image.getResourceUrl())
                .isPrimary(image.getIsPrimary())
                .createdAt(image.getCreatedAt())
                .build();
    }

    private String resolvePrimaryImageUrl(List<VehicleImageEntity> images) {
        if (images == null || images.isEmpty()) return null;
        return images.stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .findFirst()
                .or(() -> images.stream().min(Comparator.comparing(VehicleImageEntity::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))))
                .map(VehicleImageEntity::getResourceUrl)
                .orElse(null);
    }
}
