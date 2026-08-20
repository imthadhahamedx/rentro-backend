package com.rentro.util.mapper;

import com.rentro.dto.request.damage.DamageRequestDto;
import com.rentro.dto.response.damage.DamageImageResponseDto;
import com.rentro.dto.response.damage.DamageListItemResponseDto;
import com.rentro.dto.response.damage.DamageResponseDto;
import com.rentro.entity.DamageEntity;
import com.rentro.entity.VehicleDamageImageEntity;
import com.rentro.entity.VehicleEntity;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class DamageMapper {

    public DamageEntity toDamageEntity(DamageRequestDto dto) {
        if (dto == null) return null;
        return DamageEntity.builder()
                .description(dto.getDescription())
                .damageBy(DamageEntity.DamageBy.valueOf(dto.getDamageBy()))
                .remark(dto.getRemark())
                .isFixed(false)
                .build();
    }

    /** Applies request fields onto an existing managed entity (used for update). */
    public void updateDamageFromDto(DamageEntity damageEntity, DamageRequestDto dto) {
        damageEntity.setDescription(dto.getDescription());
        damageEntity.setDamageBy(DamageEntity.DamageBy.valueOf(dto.getDamageBy()));
        damageEntity.setRemark(dto.getRemark());
    }

    public DamageListItemResponseDto toDamageListItemResponseDto(DamageEntity damageEntity) {
        if (damageEntity == null) return null;
        VehicleEntity vehicleEntity = damageEntity.getVehicle();
        return DamageListItemResponseDto.builder()
                .id(damageEntity.getId())
                .vehicleId(vehicleEntity != null ? vehicleEntity.getId() : null)
                .vehicleLabel(vehicleLabel(vehicleEntity))
                .vehicleRegNo(vehicleEntity != null ? vehicleEntity.getRegNo() : null)
                .description(damageEntity.getDescription())
                .isFixed(damageEntity.getIsFixed())
                .damageBy(damageEntity.getDamageBy() != null ? damageEntity.getDamageBy().name() : null)
                .createdAt(damageEntity.getCreatedAt())
                .markedByName(damageEntity.getMarkedBy() != null ? damageEntity.getMarkedBy().getFullName() : null)
                .primaryImageUrl(resolvePrimaryImageUrl(damageEntity.getDamageImages()))
                .imageCount(damageEntity.getDamageImages() == null ? 0 : damageEntity.getDamageImages().size())
                .build();
    }

    public DamageResponseDto toDamageResponseDto(DamageEntity damageEntity) {
        if (damageEntity == null) return null;
        VehicleEntity vehicleEntity = damageEntity.getVehicle();
        return DamageResponseDto.builder()
                .id(damageEntity.getId())
                .description(damageEntity.getDescription())
                .isFixed(damageEntity.getIsFixed())
                .damageBy(damageEntity.getDamageBy() != null ? damageEntity.getDamageBy().name() : null)
                .createdAt(damageEntity.getCreatedAt())
                .fixedAt(damageEntity.getFixedAt())
                .remark(damageEntity.getRemark())
                .vehicleId(vehicleEntity != null ? vehicleEntity.getId() : null)
                .vehicleMake(vehicleEntity != null ? vehicleEntity.getMake() : null)
                .vehicleModel(vehicleEntity != null ? vehicleEntity.getModel() : null)
                .vehicleRegNo(vehicleEntity != null ? vehicleEntity.getRegNo() : null)
                .vehicleLabel(vehicleLabel(vehicleEntity))
                .markedById(damageEntity.getMarkedBy() != null ? damageEntity.getMarkedBy().getId() : null)
                .markedByName(damageEntity.getMarkedBy() != null ? damageEntity.getMarkedBy().getFullName() : null)
                .images(
                        damageEntity.getDamageImages() == null ? List.of() :
                                damageEntity.getDamageImages().stream().map(this::toDamageImageResponseDto).toList()
                )
                .build();
    }

    public DamageImageResponseDto toDamageImageResponseDto(VehicleDamageImageEntity image) {
        if (image == null) return null;
        return DamageImageResponseDto.builder()
                .id(image.getId())
                .fileName(image.getFileName())
                .url(image.getResourceUrl())
                .createdAt(image.getCreatedAt())
                .build();
    }

    private String vehicleLabel(VehicleEntity vehicleEntity) {
        if (vehicleEntity == null) return null;
        return "%s %s (%s)".formatted(vehicleEntity.getMake(), vehicleEntity.getModel(), vehicleEntity.getRegNo());
    }

    private String resolvePrimaryImageUrl(List<VehicleDamageImageEntity> images) {
        if (images == null || images.isEmpty()) return null;
        return images.stream()
                .min(Comparator.comparing(VehicleDamageImageEntity::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(VehicleDamageImageEntity::getResourceUrl)
                .orElse(null);
    }
}
