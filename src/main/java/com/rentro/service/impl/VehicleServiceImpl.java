package com.rentro.service.impl;

import com.rentro.dto.request.vehicle.VehicleRequestDto;
import com.rentro.dto.request.vehicle.VehicleStatusUpdateRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.vehicle.VehicleListItemDto;
import com.rentro.dto.response.vehicle.VehicleResponseDto;
import com.rentro.entity.SpecsEntity;
import com.rentro.entity.VehicleCategoryEntity;
import com.rentro.entity.VehicleEntity;
import com.rentro.entity.VehicleImageEntity;
import com.rentro.exception.DuplicateEntryException;
import com.rentro.exception.EntryNotFoundException;
import com.rentro.exception.ValidationException;
import com.rentro.repository.SpecsRepository;
import com.rentro.repository.VehicleCategoryRepository;
import com.rentro.repository.VehicleImageRepository;
import com.rentro.repository.VehicleRepository;
import com.rentro.service.S3StorageService;
import com.rentro.service.VehicleService;
import com.rentro.util.mapper.VehicleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleCategoryRepository vehicleCategoryRepository;
    private final VehicleImageRepository vehicleImageRepository;
    private final SpecsRepository specsRepository;
    private final VehicleMapper vehicleMapper;
    private final S3StorageService s3StorageService;

    private static final String IMAGE_KEY_PREFIX = "vehicles/%s";

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDto search(String searchText, UUID categoryId, String status, int page, int size) {
        VehicleEntity.Status statusEnum = parseStatusOrNull(status);

        var pageResult = vehicleRepository.search(searchText == null ? "" : searchText, categoryId, statusEnum, PageRequest.of(page, size));

        return PaginatedResponseDto.<VehicleListItemDto>builder()
                .count(pageResult.getTotalElements())
                .dataList(pageResult.getContent().stream().map(vehicleMapper::toVehicleListItemDTO).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDto findById(UUID id) {
        return vehicleMapper.toVehicleResponseDto(getVehicleWithDetailsOrThrow(id));
    }

    @Override
    public VehicleResponseDto create(VehicleRequestDto dto, List<MultipartFile> images) {
        if (vehicleRepository.existsByRegNoIgnoreCase(dto.getRegNo())) {
            throw new DuplicateEntryException("A vehicle with registration number '" + dto.getRegNo() + "' already exists");
        }

        VehicleCategoryEntity category = vehicleCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntryNotFoundException("Vehicle category not found"));

        VehicleEntity vehicle = vehicleMapper.toVehicleEntity(dto);
        vehicle.setVehicleCategory(category);
        vehicle = vehicleRepository.save(vehicle);

        attachSpecs(vehicle, dto.getSpecIds());

        if (!CollectionUtils.isEmpty(images)) {
            persistImages(vehicle, images);
        }

        return vehicleMapper.toVehicleResponseDto(getVehicleWithDetailsOrThrow(vehicle.getId()));
    }

    @Override
    public VehicleResponseDto update(UUID id, VehicleRequestDto dto, List<MultipartFile> newImages) {
        VehicleEntity vehicleEntity = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Vehicle not found"));

        if (vehicleRepository.existsByRegNoIgnoreCaseAndIdNot(dto.getRegNo(), id)) {
            throw new DuplicateEntryException("A vehicle with registration number '" + dto.getRegNo() + "' already exists");
        }

        if (dto.getCategoryId() != null &&
                (vehicleEntity.getVehicleCategory() == null || !dto.getCategoryId().equals(vehicleEntity.getVehicleCategory().getId()))) {
            VehicleCategoryEntity category = vehicleCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntryNotFoundException("Vehicle category not found"));
            vehicleEntity.setVehicleCategory(category);
        }

        vehicleMapper.updateVehicleFromDto(vehicleEntity, dto);
        vehicleEntity = vehicleRepository.save(vehicleEntity);

        if (dto.getSpecIds() != null) {
            syncSpecs(vehicleEntity, dto.getSpecIds());
        }

        if (!CollectionUtils.isEmpty(newImages)) {
            persistImages(vehicleEntity, newImages);
        }

        return vehicleMapper.toVehicleResponseDto(getVehicleWithDetailsOrThrow(vehicleEntity.getId()));
    }

    @Override
    public void updateStatus(UUID id, VehicleStatusUpdateRequestDto dto) {
        VehicleEntity vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Vehicle not found"));
        vehicle.setStatus(VehicleEntity.Status.valueOf(dto.getStatus()));
        vehicleRepository.save(vehicle);
    }

    @Override
    public void deleteById(UUID id) {
        VehicleEntity vehicleEntity = getVehicleWithDetailsOrThrow(id);

        // Clean up S3 objects first - cascade will remove the DB rows.
        List<VehicleImageEntity> images = vehicleImageRepository.findByVehicleId(vehicleEntity.getId());
        images.forEach(image -> s3StorageService.delete(image.getDirectory()));

        // Detach from specs (owning side) to avoid dangling join-table rows.
        if (!CollectionUtils.isEmpty(vehicleEntity.getSpecs())) {
            for (SpecsEntity spec : new ArrayList<>(vehicleEntity.getSpecs())) {
                spec.getVehicles().remove(vehicleEntity);
                specsRepository.save(spec);
            }
        }

        vehicleRepository.deleteById(id);
    }

    @Override
    public VehicleResponseDto addImages(UUID vehicleId, List<MultipartFile> images) {
        if (CollectionUtils.isEmpty(images)) {
            throw new ValidationException("At least one image file is required");
        }
        VehicleEntity vehicleEntity = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntryNotFoundException("Vehicle not found"));

        persistImages(vehicleEntity, images);

        return vehicleMapper.toVehicleResponseDto(getVehicleWithDetailsOrThrow(vehicleId));
    }

    @Override
    public void deleteImage(UUID vehicleId, UUID imageId) {
        VehicleImageEntity image = vehicleImageRepository.findById(imageId)
                .orElseThrow(() -> new EntryNotFoundException("Vehicle image not found"));

        if (!image.getVehicle().getId().equals(vehicleId)) {
            throw new ValidationException("Image does not belong to the specified vehicle");
        }

        boolean wasPrimary = Boolean.TRUE.equals(image.getIsPrimary());

        s3StorageService.delete(image.getDirectory());
        vehicleImageRepository.delete(image);

        if (wasPrimary) {
            vehicleImageRepository.findByVehicleId(vehicleId).stream()
                    .findFirst()
                    .ifPresent(next -> {
                        next.setIsPrimary(true);
                        vehicleImageRepository.save(next);
                    });
        }
    }

    @Override
    public void setPrimaryImage(UUID vehicleId, UUID imageId) {
        List<VehicleImageEntity> images = vehicleImageRepository.findByVehicleId(vehicleId);
        if (images.isEmpty()) {
            throw new EntryNotFoundException("Vehicle has no images");
        }

        boolean found = false;
        for (VehicleImageEntity image : images) {
            boolean isTarget = image.getId().equals(imageId);
            image.setIsPrimary(isTarget);
            found = found || isTarget;
        }

        if (!found) {
            throw new EntryNotFoundException("Vehicle image not found");
        }

        vehicleImageRepository.saveAll(images);
    }

    // ─── helpers ──────────────────────────────────────────────────────────

    private VehicleEntity getVehicleWithDetailsOrThrow(UUID id) {
        return vehicleRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntryNotFoundException("Vehicle not found"));
    }

    private void persistImages(VehicleEntity vehicle, List<MultipartFile> images) {
        long existingCount = vehicleImageRepository.countByVehicleId(vehicle.getId());
        String keyPrefix = IMAGE_KEY_PREFIX.formatted(vehicle.getId());

        List<VehicleImageEntity> toSave = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            MultipartFile file = images.get(i);
            S3StorageService.UploadedFile uploaded = s3StorageService.upload(file, keyPrefix);

            toSave.add(VehicleImageEntity.builder()
                    .vehicle(vehicle)
                    .fileName(uploaded.originalFileName())
                    .directory(uploaded.key())
                    .resourceUrl(uploaded.url())
                    .hash(uploaded.contentType())
                    .isPrimary(existingCount == 0 && i == 0)
                    .build());
        }

        vehicleImageRepository.saveAll(toSave);
    }

    private void attachSpecs(VehicleEntity vehicle, List<UUID> specIds) {
        if (CollectionUtils.isEmpty(specIds)) {
            return;
        }
        List<SpecsEntity> specs = specsRepository.findAllById(specIds);
        if (specs.size() != new HashSet<>(specIds).size()) {
            throw new EntryNotFoundException("One or more specifications were not found");
        }
        for (SpecsEntity spec : specs) {
            if (spec.getVehicles() == null) {
                spec.setVehicles(new ArrayList<>());
            }
            if (!spec.getVehicles().contains(vehicle)) {
                spec.getVehicles().add(vehicle);
            }
        }
        specsRepository.saveAll(specs);
    }

    private void syncSpecs(VehicleEntity vehicle, List<UUID> newSpecIds) {
        Set<UUID> desired = new HashSet<>(newSpecIds);

        // Remove vehicle from specs no longer selected.
        if (!CollectionUtils.isEmpty(vehicle.getSpecs())) {
            for (SpecsEntity spec : new ArrayList<>(vehicle.getSpecs())) {
                if (!desired.contains(spec.getId())) {
                    spec.getVehicles().remove(vehicle);
                    specsRepository.save(spec);
                }
            }
        }

        attachSpecs(vehicle, newSpecIds);
    }

    private VehicleEntity.Status parseStatusOrNull(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return VehicleEntity.Status.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid status: " + status);
        }
    }
}
