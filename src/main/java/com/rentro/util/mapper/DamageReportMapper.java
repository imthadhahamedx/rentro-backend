package com.rentro.util.mapper;

import com.rentro.dto.response.damage.DamageImageResponseDto;
import com.rentro.dto.response.damage_report.DamageReportDetailResponseDto;
import com.rentro.dto.response.damage_report.DamageReportListItemResponseDto;
import com.rentro.entity.*;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class DamageReportMapper {

    public DamageReportListItemResponseDto toDamageReportListItemResponseDto(DamageReportEntity report) {
        if (report == null) return null;

        BookingEntity bookingEntity = report.getBooking();
        DamageEntity damageEntity = report.getDamage();
        VehicleEntity vehicleEntity = damageEntity != null ? damageEntity.getVehicle() : null;
        CustomerEntity customerEntity = bookingEntity != null ? bookingEntity.getCustomer() : null;

        return DamageReportListItemResponseDto.builder()
                .id(report.getId())
                .date(report.getDate())

                .bookingId(bookingEntity != null ? bookingEntity.getId() : null)
                .bookingRef(bookingEntity != null ? bookingEntity.getBookingRef() : null)
                .customerName(customerEntity != null ? customerEntity.getUser().getFullName() : null)

                .damageId(damageEntity != null ? damageEntity.getId() : null)
                .vehicleLabel(vehicleLabel(vehicleEntity))
                .vehicleRegNo(vehicleEntity != null ? vehicleEntity.getRegNo() : null)
                .damageDescription(damageEntity != null ? damageEntity.getDescription() : null)
                .damageIsFixed(damageEntity != null ? damageEntity.getIsFixed() : null)
                .damageBy(damageEntity != null && damageEntity.getDamageBy() != null ? damageEntity.getDamageBy().name() : null)
                .primaryImageUrl(resolvePrimaryImageUrl(damageEntity))

                .reviewedById(report.getReviewedBy() != null ? report.getReviewedBy().getId() : null)
                .reviewedByName(report.getReviewedBy() != null ? report.getReviewedBy().getFullName() : null)
                .reviewed(report.getReviewedBy() != null)
                .build();
    }

    public DamageReportDetailResponseDto toDamageReportDetailResponseDto(DamageReportEntity report) {
        if (report == null) return null;

        BookingEntity bookingEntity = report.getBooking();
        DamageEntity damageEntity = report.getDamage();
        VehicleEntity vehicleEntity = damageEntity != null ? damageEntity.getVehicle() : null;
        CustomerEntity customerEntity = bookingEntity != null ? bookingEntity.getCustomer() : null;

        return DamageReportDetailResponseDto.builder()
                .id(report.getId())

                .date(report.getDate())
                .bookingId(bookingEntity != null ? bookingEntity.getId() : null)
                .bookingRef(bookingEntity!= null ? bookingEntity.getBookingRef() : null)
                .bookingStatus(bookingEntity!= null && bookingEntity.getStatus() != null ? bookingEntity.getStatus().name() : null)
                .customerId(customerEntity != null ? customerEntity.getId() : null)
                .customerName(customerEntity != null ? customerEntity.getUser().getFullName() : null)
                .customerPhone(customerEntity != null ? customerEntity.getUser().getPhoneNumber() : null)
                .pickupDate(bookingEntity!= null && bookingEntity.getPickupDate() != null ? bookingEntity.getPickupDate().toString() : null)
                .dropoffDate(bookingEntity!= null && bookingEntity.getDropoffDate() != null ? bookingEntity.getDropoffDate().toString() : null)

                .damageId(damageEntity != null ? damageEntity.getId() : null)
                .damageDescription(damageEntity != null ? damageEntity.getDescription() : null)
                .damageIsFixed(damageEntity != null ? damageEntity.getIsFixed() : null)
                .damageBy(damageEntity != null && damageEntity.getDamageBy() != null ? damageEntity.getDamageBy().name() : null)
                .damageCreatedAt(damageEntity != null ? damageEntity.getCreatedAt() : null)
                .damageFixedAt(damageEntity != null ? damageEntity.getFixedAt() : null)
                .damageRemark(damageEntity != null ? damageEntity.getRemark() : null)
                .damageImages(mapDamageImages(damageEntity))

                .vehicleId(vehicleEntity != null ? vehicleEntity.getId() : null)
                .vehicleLabel(vehicleLabel(vehicleEntity))
                .vehicleRegNo(vehicleEntity != null ? vehicleEntity.getRegNo() : null)

                .reviewedById(report.getReviewedBy() != null ? report.getReviewedBy().getId() : null)
                .reviewedByName(report.getReviewedBy() != null ? report.getReviewedBy().getFullName() : null)
                .reviewed(report.getReviewedBy() != null)
                .build();
    }

    // ─── helpers ──────────────────────────────────────────────────────────
    private String vehicleLabel(VehicleEntity vehicleEntity) {
        if (vehicleEntity == null) return null;
        return "%s %s (%s)".formatted(vehicleEntity.getMake(), vehicleEntity.getModel(), vehicleEntity.getRegNo());
    }

    private String resolvePrimaryImageUrl(DamageEntity damageEntity) {
        if (damageEntity == null || damageEntity.getDamageImages() == null || damageEntity.getDamageImages().isEmpty()) return null;
        return damageEntity.getDamageImages().stream()
                .min(Comparator.comparing(VehicleDamageImageEntity::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(VehicleDamageImageEntity::getResourceUrl)
                .orElse(null);
    }

    private List<DamageImageResponseDto> mapDamageImages(DamageEntity damageEntity) {
        if (damageEntity == null || damageEntity.getDamageImages() == null) return List.of();
        return damageEntity.getDamageImages().stream()
                .map(img -> DamageImageResponseDto.builder()
                        .id(img.getId())
                        .fileName(img.getFileName())
                        .url(img.getResourceUrl())
                        .createdAt(img.getCreatedAt())
                        .build())
                .toList();
    }
}
