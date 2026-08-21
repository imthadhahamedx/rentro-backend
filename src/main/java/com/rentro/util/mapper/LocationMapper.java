package com.rentro.util.mapper;

import com.rentro.dto.request.location.LocationRequestDto;
import com.rentro.dto.response.location.LocationResponseDto;
import com.rentro.entity.LocationEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LocationMapper {

    public LocationEntity toLocationEntity(LocationRequestDto dto) {
        if (dto == null) return null;
        return LocationEntity.builder()
                .locationName(dto.getLocationName())
                .address(dto.getAddress())
                .city(dto.getCity())
                .latitude(dto.getLatitude() != null ? BigDecimal.valueOf(dto.getLatitude()) : null)
                .longitude(dto.getLongitude() != null ? BigDecimal.valueOf(dto.getLongitude()) : null)
                .isActive(dto.getIsActive() == null || dto.getIsActive())
                .build();
    }

    public LocationResponseDto toLocationResponseDto(LocationEntity locationEntity, long pickupBookingCount, long dropoffBookingCount) {
        if (locationEntity == null) return null;
        return LocationResponseDto.builder()
                .id(locationEntity.getId())
                .locationName(locationEntity.getLocationName())
                .address(locationEntity.getAddress())
                .city(locationEntity.getCity())
                .latitude(locationEntity.getLatitude() != null ? locationEntity.getLatitude().doubleValue() : null)
                .longitude(locationEntity.getLongitude() != null ? locationEntity.getLongitude().doubleValue() : null)
                .isActive(locationEntity.getIsActive())
                .createdAt(locationEntity.getCreatedAt())
                .pickupBookingCount(pickupBookingCount)
                .dropoffBookingCount(dropoffBookingCount)
                .totalBookingCount(pickupBookingCount + dropoffBookingCount)
                .build();
    }
}
