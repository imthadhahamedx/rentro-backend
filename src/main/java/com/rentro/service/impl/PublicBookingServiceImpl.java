package com.rentro.service.impl;

import com.rentro.dto.request.PublicBookingRequestDto;
import com.rentro.dto.response.PublicBookingResponseDto;
import com.rentro.entity.VehicleEntity;
import com.rentro.entity.PublicBookingRequestEntity;
import com.rentro.exception.EntryNotFoundException;
import com.rentro.exception.ValidationException;
import com.rentro.repository.VehicleRepository;
import com.rentro.repository.PublicBookingRequestRepository;
import com.rentro.service.PublicBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PublicBookingServiceImpl implements PublicBookingService {

    private final PublicBookingRequestRepository requestRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public PublicBookingResponseDto createRequest(PublicBookingRequestDto dto) {

        // ── Date validation ──────────────────────────────────────────────────
        if (!dto.getReturnDate().isAfter(dto.getPickupDate())) {
            throw new ValidationException("Return date must be after pickup date");
        }

        // ── Vehicle lookup ───────────────────────────────────────────────────
        VehicleEntity vehicleEntity = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new EntryNotFoundException("Vehicle not found"));

        if (vehicleEntity.getStatus() != VehicleEntity.Status.AVAILABLE) {
            throw new ValidationException("Selected vehicle is no longer available");
        }

        // ── Estimate ─────────────────────────────────────────────────────────
        int days = (int) Math.max(1, ChronoUnit.DAYS.between(dto.getPickupDate(), dto.getReturnDate()));
        BigDecimal estimatedAmount = vehicleEntity.getDailyRate().multiply(BigDecimal.valueOf(days));

        // ── Persist ──────────────────────────────────────────────────────────
        PublicBookingRequestEntity request = PublicBookingRequestEntity.builder()
                .bookingRef(generateRef())
                .customerName(dto.getCustomerName().trim())
                .contactNumber(dto.getContactNumber().trim())
                .pickupLocation(dto.getPickupLocation())
                .pickupDate(dto.getPickupDate())
                .returnDate(dto.getReturnDate())
                .totalDays(days)
                .estimatedAmount(estimatedAmount)
                .vehicle(vehicleEntity)
                .vehicleName(vehicleEntity.getMake())
                .status(PublicBookingRequestEntity.RequestStatus.PENDING)
                .build();

        PublicBookingRequestEntity saved = requestRepository.save(request);

        return PublicBookingResponseDto.builder()
                .bookingRef(saved.getBookingRef())
                .vehicleName(saved.getVehicleName())
                .pickupDate(saved.getPickupDate())
                .returnDate(saved.getReturnDate())
                .estimatedAmount(saved.getEstimatedAmount())
                .status(saved.getStatus().name())
                .build();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private String generateRef() {
        String ref;
        do {
            ref = "VR" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                  + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        } while (requestRepository.existsByBookingRef(ref));
        return ref;
    }
}
