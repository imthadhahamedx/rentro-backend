package com.rentro.dto.response.vehicle;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VehicleImageResponseDto {
    private UUID id;
    private String fileName;
    private String url;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
}
