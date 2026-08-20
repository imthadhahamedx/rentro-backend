package com.rentro.service.impl;

import com.rentro.dto.request.damage.DamageFixedUpdateRequestDto;
import com.rentro.dto.request.damage.DamageRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.damage.DamageListItemResponseDto;
import com.rentro.dto.response.damage.DamageResponseDto;
import com.rentro.entity.DamageEntity;
import com.rentro.entity.UserEntity;
import com.rentro.entity.VehicleDamageImageEntity;
import com.rentro.entity.VehicleEntity;
import com.rentro.exception.EntryNotFoundException;
import com.rentro.exception.ValidationException;
import com.rentro.repository.DamageRepository;
import com.rentro.repository.UserRepository;
import com.rentro.repository.VehicleDamageImageRepository;
import com.rentro.repository.VehicleRepository;
import com.rentro.service.DamageService;
import com.rentro.service.S3StorageService;
import com.rentro.util.mapper.DamageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DamageServiceImpl implements DamageService {

    private final DamageRepository damageRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleDamageImageRepository vehicleDamageImageRepository;
    private final UserRepository userRepository;
    private final DamageMapper damageMapper;
    private final S3StorageService s3StorageService;

    private static final String IMAGE_KEY_PREFIX = "damage/%s";

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDto search(String searchText, String status, String damageBy, UUID vehicleId, int page, int size) {
        Boolean isFixed = parseFixedFilterOrNull(status);
        DamageEntity.DamageBy damageByEnum = parseDamageByOrNull(damageBy);

        var pageResult = damageRepository.search(
                searchText == null ? "" : searchText, isFixed, damageByEnum, vehicleId, PageRequest.of(page, size));

        return PaginatedResponseDto.<DamageListItemResponseDto>builder()
                .count(pageResult.getTotalElements())
                .dataList(pageResult.getContent().stream().map(damageMapper::toDamageListItemResponseDto).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DamageResponseDto findById(UUID id) {
        return damageMapper.toDamageResponseDto(getDamageWithDetailsOrThrow(id));
    }

    @Override
    public DamageResponseDto create(DamageRequestDto dto, List<MultipartFile> images) {
        VehicleEntity vehicleEntity = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new EntryNotFoundException("Vehicle not found"));

        UserEntity currentUser = getCurrentUserOrNull();
        if (currentUser == null) {
            throw new ValidationException("Could not resolve the authenticated user for this damage report");
        }

        DamageEntity damageEntity = damageMapper.toDamageEntity(dto);
        damageEntity.setVehicle(vehicleEntity);
        damageEntity.setMarkedBy(currentUser);
        damageEntity = damageRepository.save(damageEntity);

        if (!CollectionUtils.isEmpty(images)) {
            persistImages(damageEntity, images);
        }

        return damageMapper.toDamageResponseDto(getDamageWithDetailsOrThrow(damageEntity.getId()));
    }

    @Override
    public DamageResponseDto update(UUID id, DamageRequestDto dto, List<MultipartFile> newImages) {
        DamageEntity damageEntity = damageRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Damage record not found"));

        if (dto.getVehicleId() != null &&
                (damageEntity.getVehicle() == null || !dto.getVehicleId().equals(damageEntity.getVehicle().getId()))) {
            VehicleEntity vehicleEntity = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new EntryNotFoundException("Vehicle not found"));
            damageEntity.setVehicle(vehicleEntity);
        }

        damageMapper.updateDamageFromDto(damageEntity, dto);
        damageEntity = damageRepository.save(damageEntity);

        if (!CollectionUtils.isEmpty(newImages)) {
            persistImages(damageEntity, newImages);
        }

        return damageMapper.toDamageResponseDto(getDamageWithDetailsOrThrow(damageEntity.getId()));
    }

    @Override
    public void updateFixedStatus(UUID id, DamageFixedUpdateRequestDto dto) {
        DamageEntity damageEntity = damageRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Damage record not found"));

        boolean isFixed = Boolean.TRUE.equals(dto.getIsFixed());
        damageEntity.setIsFixed(isFixed);
        damageEntity.setFixedAt(isFixed ? LocalDateTime.now() : null);
        if (dto.getRemark() != null) {
            damageEntity.setRemark(dto.getRemark());
        }

        damageRepository.save(damageEntity);
    }

    @Override
    public void deleteById(UUID id) {
        DamageEntity damageEntity = getDamageWithDetailsOrThrow(id);

        // Clean up S3 objects first - cascade will remove the DB rows.
        List<VehicleDamageImageEntity> images = vehicleDamageImageRepository.findByDamageId(damageEntity.getId());
        images.forEach(image -> s3StorageService.delete(image.getDirectory()));

        damageRepository.deleteById(id);
    }

    @Override
    public DamageResponseDto addImages(UUID damageId, List<MultipartFile> images) {
        if (CollectionUtils.isEmpty(images)) {
            throw new ValidationException("At least one image file is required");
        }
        DamageEntity damageEntity = damageRepository.findById(damageId)
                .orElseThrow(() -> new EntryNotFoundException("Damage record not found"));

        persistImages(damageEntity, images);

        return damageMapper.toDamageResponseDto(getDamageWithDetailsOrThrow(damageId));
    }

    @Override
    public void deleteImage(UUID damageId, UUID imageId) {
        VehicleDamageImageEntity image = vehicleDamageImageRepository.findById(imageId)
                .orElseThrow(() -> new EntryNotFoundException("Damage image not found"));

        if (!image.getDamage().getId().equals(damageId)) {
            throw new ValidationException("Image does not belong to the specified damage record");
        }

        s3StorageService.delete(image.getDirectory());
        vehicleDamageImageRepository.delete(image);
    }

    // ─── helpers ──────────────────────────────────────────────────────────
    private DamageEntity getDamageWithDetailsOrThrow(UUID id) {
        return damageRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntryNotFoundException("Damage record not found"));
    }

    private void persistImages(DamageEntity damageEntity, List<MultipartFile> images) {
        String keyPrefix = IMAGE_KEY_PREFIX.formatted(damageEntity.getId());

        List<VehicleDamageImageEntity> toSave = new ArrayList<>();
        for (MultipartFile file : images) {
            S3StorageService.UploadedFile uploaded = s3StorageService.upload(file, keyPrefix);

            toSave.add(VehicleDamageImageEntity.builder()
                    .damage(damageEntity)
                    .fileName(uploaded.originalFileName())
                    .directory(uploaded.key())
                    .resourceUrl(uploaded.url())
                    .hash(uploaded.contentType())
                    .build());
        }

        vehicleDamageImageRepository.saveAll(toSave);
    }

    private Boolean parseFixedFilterOrNull(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        return switch (status.trim().toUpperCase()) {
            case "FIXED" -> true;
            case "OPEN" -> false;
            default -> throw new ValidationException("Invalid status: " + status + " (expected OPEN or FIXED)");
        };
    }

    private DamageEntity.DamageBy parseDamageByOrNull(String damageBy) {
        if (damageBy == null || damageBy.isBlank()) {
            return null;
        }
        try {
            return DamageEntity.DamageBy.valueOf(damageBy.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid damage by: " + damageBy);
        }
    }

    private UserEntity getCurrentUserOrNull() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) return null;
        return userRepository.findUserEntityByEmail(authentication.getName()).orElse(null);
    }
}
