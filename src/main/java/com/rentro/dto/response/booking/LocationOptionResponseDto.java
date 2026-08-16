package com.rentro.dto.response.booking;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LocationOptionResponseDto {
    private UUID id;
    private String locationName;
    private String city;
}
