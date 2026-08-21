package com.rentro.dto.response.location;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LocationResponseDto {
    private UUID id;
    private String locationName;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Long pickupBookingCount;
    private Long dropoffBookingCount;
    private Long totalBookingCount;
}
