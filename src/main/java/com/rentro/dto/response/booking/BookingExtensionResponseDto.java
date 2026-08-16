package com.rentro.dto.response.booking;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingExtensionResponseDto {
    private UUID id;
    private LocalDate originalDropoffDate;
    private LocalDate newDropoffDate;
    private Integer additionalDays;
    private BigDecimal additionalAmount;
    private String status;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private String approvedByName;
}
