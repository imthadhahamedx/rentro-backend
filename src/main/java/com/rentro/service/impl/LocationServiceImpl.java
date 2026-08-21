package com.rentro.service.impl;

import com.rentro.dto.request.location.LocationRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.location.LocationResponseDto;
import com.rentro.entity.LocationEntity;
import com.rentro.exception.DuplicateEntryException;
import com.rentro.exception.EntryNotFoundException;
import com.rentro.exception.ValidationException;
import com.rentro.repository.BookingRepository;
import com.rentro.repository.LocationRepository;
import com.rentro.service.LocationService;
import com.rentro.util.mapper.LocationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final BookingRepository bookingRepository;
    private final LocationMapper locationMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDto findAllForAdmin(String searchText, Boolean isActive, int page, int size) {
        var result = locationRepository.search(searchText, isActive, PageRequest.of(page, size));
        return PaginatedResponseDto.<LocationResponseDto>builder()
                .count(result.getTotalElements())
                .dataList(
                        result.getContent().stream()
                                .map(this::toResponseWithCounts)
                                .toList()
                )
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public LocationResponseDto findById(UUID id) {
        LocationEntity locationEntity = getLocationOrThrow(id);
        return toResponseWithCounts(locationEntity);
    }

    @Override
    public LocationResponseDto create(LocationRequestDto dto) {
        assertNotDuplicate(dto.getLocationName(), dto.getCity(), null);
        LocationEntity locationEntity = locationMapper.toLocationEntity(dto);
        LocationEntity saved = locationRepository.save(locationEntity);
        return toResponseWithCounts(saved);
    }

    @Override
    public LocationResponseDto update(UUID id, LocationRequestDto dto) {
        LocationEntity locationEntity = getLocationOrThrow(id);
        assertNotDuplicate(dto.getLocationName(), dto.getCity(), id);

        locationEntity.setLocationName(dto.getLocationName());
        locationEntity.setAddress(dto.getAddress());
        locationEntity.setCity(dto.getCity());
        locationEntity.setLatitude(dto.getLatitude() != null ? BigDecimal.valueOf(dto.getLatitude()) : null);
        locationEntity.setLongitude(dto.getLongitude() != null ? BigDecimal.valueOf(dto.getLongitude()) : null);
        if (dto.getIsActive() != null) {
            locationEntity.setIsActive(dto.getIsActive());
        }

        LocationEntity saved = locationRepository.save(locationEntity);
        return toResponseWithCounts(saved);
    }

    @Override
    public void updateStatus(UUID id, boolean isActive) {
        LocationEntity locationEntity = getLocationOrThrow(id);
        locationEntity.setIsActive(isActive);
        locationRepository.save(locationEntity);
    }

    @Override
    public void deleteById(UUID id) {
        LocationEntity locationEntity = getLocationOrThrow(id);

        long pickupCount = bookingRepository.countByPickupLocationId(id);
        long dropoffCount = bookingRepository.countByDropoffLocationId(id);
        long totalCount = pickupCount + dropoffCount;

        if (totalCount > 0) {
            throw new ValidationException(
                    "Cannot delete a location with existing bookings (" + totalCount +
                            "). Deactivate it instead."
            );
        }

        locationRepository.delete(locationEntity);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────
    private LocationEntity getLocationOrThrow(UUID id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Location not found"));
    }

    private void assertNotDuplicate(String locationName, String city, UUID excludeId) {
        boolean exists = locationRepository.existsByLocationNameIgnoreCaseAndCityIgnoreCase(locationName, city);
        if (!exists) return;

        if (excludeId != null) {
            LocationEntity existing = locationRepository.findById(excludeId).orElse(null);
            boolean isSameRecord = existing != null
                    && existing.getLocationName().equalsIgnoreCase(locationName)
                    && existing.getCity().equalsIgnoreCase(city);
            if (isSameRecord) return;
        }

        throw new DuplicateEntryException("A location with this name already exists in " + city);
    }

    private LocationResponseDto toResponseWithCounts(LocationEntity locationEntity) {
        long pickupCount = bookingRepository.countByPickupLocationId(locationEntity.getId());
        long dropoffCount = bookingRepository.countByDropoffLocationId(locationEntity.getId());
        return locationMapper.toLocationResponseDto(locationEntity, pickupCount, dropoffCount);
    }
}
