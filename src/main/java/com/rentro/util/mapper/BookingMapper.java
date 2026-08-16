package com.rentro.util.mapper;

import com.rentro.dto.response.booking.*;
import com.rentro.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BookingMapper {

    public CustomerOptionResponseDto toCustomerOptionResponseDto(CustomerEntity customerEntity) {
        if (customerEntity == null) return null;
        UserEntity userEntity = customerEntity.getUser();
        return CustomerOptionResponseDto.builder()
                .id(customerEntity.getId())
                .fullName(userEntity != null ? userEntity.getFullName() : null)
                .phoneNumber(userEntity != null ? userEntity.getPhoneNumber() : null)
                .email(userEntity != null ? userEntity.getEmail() : null)
                .nic(customerEntity.getNic())
                .drivingLicenseNo(customerEntity.getDrivingLicenseNo())
                .build();
    }

    public VehicleOptionResponseDto toVehicleOptionResponseDto(VehicleEntity vehicleEntity) {
        if (vehicleEntity == null) return null;
        return VehicleOptionResponseDto.builder()
                .id(vehicleEntity.getId())
                .make(vehicleEntity.getMake())
                .model(vehicleEntity.getModel())
                .modelYear(vehicleEntity.getModelYear())
                .regNo(vehicleEntity.getRegNo())
                .categoryName(vehicleEntity.getVehicleCategory() != null ? vehicleEntity.getVehicleCategory().getCategory() : null)
                .dailyRate(vehicleEntity.getDailyRate())
                .status(vehicleEntity.getStatus() != null ? vehicleEntity.getStatus().name() : null)
                .build();
    }

    public LocationOptionResponseDto toLocationOptionResponseDto(LocationEntity locationEntity) {
        if (locationEntity == null) return null;
        return LocationOptionResponseDto.builder()
                .id(locationEntity.getId())
                .locationName(locationEntity.getLocationName())
                .city(locationEntity.getCity())
                .build();
    }

    public BookingListItemResponseDto toBookingListItemResponseDto(BookingEntity bookingEntity) {
        if (bookingEntity == null) return null;
        VehicleEntity vehicle = bookingEntity.getVehicle();
        UserEntity UserEntity = bookingEntity.getCustomer() != null ? bookingEntity.getCustomer().getUser() : null;

        return BookingListItemResponseDto.builder()
                .id(bookingEntity.getId())
                .bookingRef(bookingEntity.getBookingRef())
                .status(bookingEntity.getStatus() != null ? bookingEntity.getStatus().name() : null)
                .customerName(UserEntity != null ? UserEntity.getFullName() : null)
                .customerPhone(UserEntity != null ? UserEntity.getPhoneNumber() : null)
                .vehicleName(vehicle != null ? (vehicle.getMake() + " " + vehicle.getModel()) : null)
                .regNo(vehicle != null ? vehicle.getRegNo() : null)
                .pickupLocationName(bookingEntity.getPickupLocation() != null ? bookingEntity.getPickupLocation().getLocationName() : null)
                .dropoffLocationName(bookingEntity.getDropoffLocation() != null ? bookingEntity.getDropoffLocation().getLocationName() : null)
                .pickupDate(bookingEntity.getPickupDate())
                .dropoffDate(bookingEntity.getDropoffDate())
                .totalAmount(bookingEntity.getTotalAmount())
                .finalAmount(bookingEntity.getFinalAmount())
                .createdAt(bookingEntity.getCreatedAt())
                .build();
    }

    public BookingCustomerSummaryResponseDto toBookingCustomerSummaryResponseDto(CustomerEntity customerEntity) {
        if (customerEntity == null) return null;
        UserEntity userEntity = customerEntity.getUser();
        return BookingCustomerSummaryResponseDto.builder()
                .id(customerEntity.getId())
                .fullName(userEntity != null ? userEntity.getFullName() : null)
                .phoneNumber(userEntity != null ? userEntity.getPhoneNumber() : null)
                .email(userEntity != null ? userEntity.getEmail() : null)
                .nic(customerEntity.getNic())
                .drivingLicenseNo(customerEntity.getDrivingLicenseNo())
                .licenseExpiryDate(customerEntity.getLicenseExpiryDate())
                .build();
    }

    public BookingVehicleSummaryResponseDto toBookingVehicleSummaryResponseDto(VehicleEntity vehicleEntity) {
        if (vehicleEntity == null) return null;
        return BookingVehicleSummaryResponseDto.builder()
                .id(vehicleEntity.getId())
                .make(vehicleEntity.getMake())
                .model(vehicleEntity.getModel())
                .modelYear(vehicleEntity.getModelYear())
                .regNo(vehicleEntity.getRegNo())
                .colour(vehicleEntity.getColour())
                .categoryName(vehicleEntity.getVehicleCategory() != null ? vehicleEntity.getVehicleCategory().getCategory() : null)
                .dailyRate(vehicleEntity.getDailyRate())
                .status(vehicleEntity.getStatus() != null ? vehicleEntity.getStatus().name() : null)
                .build();
    }

    public BookingLocationSummaryResponseDto toBookingLocationSummaryResponseDto(LocationEntity locationEntity) {
        if (locationEntity == null) return null;
        return BookingLocationSummaryResponseDto.builder()
                .id(locationEntity.getId())
                .locationName(locationEntity.getLocationName())
                .city(locationEntity.getCity())
                .build();
    }

    public BookingExtensionResponseDto toBookingExtensionResponseDto(BookingExtensionEntity bookingExtensionEntity) {
        if (bookingExtensionEntity == null) return null;
        return BookingExtensionResponseDto.builder()
                .id(bookingExtensionEntity.getId())
                .originalDropoffDate(bookingExtensionEntity.getOriginalDropoffDate())
                .newDropoffDate(bookingExtensionEntity.getNewDropoffDate())
                .additionalDays(bookingExtensionEntity.getAdditionalDays())
                .additionalAmount(bookingExtensionEntity.getAdditionalAmount())
                .status(bookingExtensionEntity.getStatus() != null ? bookingExtensionEntity.getStatus().name() : null)
                .reason(bookingExtensionEntity.getReason())
                .createdAt(bookingExtensionEntity.getCreatedAt())
                .approvedAt(bookingExtensionEntity.getApprovedAt())
                .approvedByName(bookingExtensionEntity.getApprovedBy() != null ? bookingExtensionEntity.getApprovedBy().getFullName() : null)
                .build();
    }

    public BookingDetailResponseDto toBookingDetailResponseDto(BookingEntity bookingEntity) {
        if (bookingEntity == null) return null;

        BigDecimal totalPaid = bookingEntity.getPayments() == null ? BigDecimal.ZERO :
                bookingEntity.getPayments().stream()
                        .filter(p -> p.getStatus() == PaymentEntity.PaymentStatus.COMPLETED
                                && p.getPaymentType() != PaymentEntity.PaymentType.REFUND)
                        .map(PaymentEntity::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal finalAmount = bookingEntity.getFinalAmount() != null ? bookingEntity.getFinalAmount() : bookingEntity.getTotalAmount();
        BigDecimal balanceDue = finalAmount != null ? finalAmount.subtract(totalPaid) : BigDecimal.ZERO;

        return BookingDetailResponseDto.builder()
                .id(bookingEntity.getId())
                .bookingRef(bookingEntity.getBookingRef())
                .status(bookingEntity.getStatus() != null ? bookingEntity.getStatus().name() : null)
                .pickupDate(bookingEntity.getPickupDate())
                .dropoffDate(bookingEntity.getDropoffDate())
                .actualReturnDate(bookingEntity.getActualReturnDate())
                .totalDays(bookingEntity.getTotalDays())
                .dailyRate(bookingEntity.getDailyRate())
                .totalAmount(bookingEntity.getTotalAmount())
                .discountAmount(bookingEntity.getDiscountAmount())
                .extraCharges(bookingEntity.getExtraCharges())
                .extraChargesNote(bookingEntity.getExtraChargesNote())
                .finalAmount(bookingEntity.getFinalAmount())
                .totalPaid(totalPaid)
                .balanceDue(balanceDue)
                .notes(bookingEntity.getNotes())
                .createdAt(bookingEntity.getCreatedAt())
                .updatedAt(bookingEntity.getUpdatedAt())
                .createdByName(bookingEntity.getCreatedBy() != null ? bookingEntity.getCreatedBy().getFullName() : null)
                .approvedByName(bookingEntity.getApprovedBy() != null ? bookingEntity.getApprovedBy().getFullName() : null)
                .customer(toBookingCustomerSummaryResponseDto(bookingEntity.getCustomer()))
                .vehicle(toBookingVehicleSummaryResponseDto(bookingEntity.getVehicle()))
                .pickupLocation(toBookingLocationSummaryResponseDto(bookingEntity.getPickupLocation()))
                .dropoffLocation(toBookingLocationSummaryResponseDto(bookingEntity.getDropoffLocation()))
                .extension(toBookingExtensionResponseDto(bookingEntity.getExtension()))
                .damageReportsCount(bookingEntity.getDamageReports() == null ? 0 : bookingEntity.getDamageReports().size())
                .paymentsCount(bookingEntity.getPayments() == null ? 0 : bookingEntity.getPayments().size())
                .build();
    }
}
