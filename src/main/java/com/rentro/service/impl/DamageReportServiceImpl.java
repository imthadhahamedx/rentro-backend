package com.rentro.service.impl;

import com.rentro.dto.request.damage_report.DamageReportRequestDto;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.damage_report.DamageReportDetailResponseDto;
import com.rentro.dto.response.damage_report.DamageReportListItemResponseDto;
import com.rentro.entity.BookingEntity;
import com.rentro.entity.DamageEntity;
import com.rentro.entity.DamageReportEntity;
import com.rentro.entity.UserEntity;
import com.rentro.exception.EntryNotFoundException;
import com.rentro.exception.ValidationException;
import com.rentro.repository.BookingRepository;
import com.rentro.repository.DamageReportRepository;
import com.rentro.repository.DamageRepository;
import com.rentro.repository.UserRepository;
import com.rentro.service.DamageReportService;
import com.rentro.util.mapper.DamageReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DamageReportServiceImpl implements DamageReportService {

    private final DamageReportRepository damageReportRepository;
    private final DamageRepository damageRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final DamageReportMapper damageReportMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDto search(String searchText, String reviewed, int page, int size) {
        Boolean reviewedFilter = parseReviewedOrNull(reviewed);
        String text = searchText == null ? "" : searchText.trim();

        Page<DamageReportEntity> result = damageReportRepository.search(text, reviewedFilter, PageRequest.of(page, size));

        return PaginatedResponseDto.<DamageReportListItemResponseDto>builder()
                .count(result.getTotalElements())
                .dataList(result.getContent().stream().map(damageReportMapper::toDamageReportListItemResponseDto).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DamageReportDetailResponseDto findById(UUID id) {
        DamageReportEntity report = damageReportRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntryNotFoundException("Damage report not found"));
        return damageReportMapper.toDamageReportDetailResponseDto(report);
    }

    @Override
    public DamageReportDetailResponseDto create(DamageReportRequestDto dto) {
        BookingEntity bookingEntity = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new EntryNotFoundException("Booking not found"));

        DamageEntity damageEntity = damageRepository.findById(dto.getDamageId())
                .orElseThrow(() -> new EntryNotFoundException("Damage record not found"));

        // Guard: damage must belong to the booking's vehicle
        if (damageEntity.getVehicle() == null ||
                bookingEntity.getVehicle() == null ||
                !damageEntity.getVehicle().getId().equals(bookingEntity.getVehicle().getId())) {
            throw new ValidationException("Damage record does not belong to the vehicle on this booking");
        }

        DamageReportEntity report = DamageReportEntity.builder()
                .booking(bookingEntity)
                .damage(damageEntity)
                .build();

        report = damageReportRepository.save(report);
        return damageReportMapper.toDamageReportDetailResponseDto(
                damageReportRepository.findByIdWithDetails(report.getId())
                        .orElseThrow(() -> new EntryNotFoundException("Damage report not found after save"))
        );
    }

    @Override
    public void markAsReviewed(UUID id) {
        DamageReportEntity report = damageReportRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Damage report not found"));

        UserEntity reviewer = getCurrentUserOrThrow();
        report.setReviewedBy(reviewer);
        damageReportRepository.save(report);
    }

    @Override
    public void deleteById(UUID id) {
        if (!damageReportRepository.existsById(id)) {
            throw new EntryNotFoundException("Damage report not found");
        }
        damageReportRepository.deleteById(id);
    }

    // ─── helpers ──────────────────────────────────────────────────────────
    private Boolean parseReviewedOrNull(String reviewed) {
        if (reviewed == null || reviewed.isBlank()) return null;
        return switch (reviewed.trim().toUpperCase()) {
            case "REVIEWED" -> true;
            case "PENDING" -> false;
            default -> throw new ValidationException("Invalid reviewed filter: " + reviewed + " (expected PENDING or REVIEWED)");
        };
    }

    private UserEntity getCurrentUserOrThrow() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ValidationException("Could not resolve the authenticated user");
        }
        return userRepository.findUserEntityByEmail(authentication.getName())
                .orElseThrow(() -> new ValidationException("Authenticated user not found in database"));
    }
}
