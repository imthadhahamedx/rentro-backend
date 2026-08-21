package com.rentro.service.impl;

import com.rentro.dto.request.booking.*;
import com.rentro.dto.response.PaginatedResponseDto;
import com.rentro.dto.response.booking.*;
import com.rentro.entity.*;
import com.rentro.exception.EntryNotFoundException;
import com.rentro.exception.ValidationException;
import com.rentro.repository.*;
import com.rentro.service.BookingService;
import com.rentro.util.mapper.BookingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final LocationRepository locationRepository;
    private final BookingExtensionRepository bookingExtensionRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    // ─── Reads ──────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDto findAll(String searchText, String status, int page, int size) {
        BookingEntity.BookingStatus statusEnum = parseStatusOrNull(status);
        String text = searchText == null ? "" : searchText.trim();

        Page<BookingEntity> result = bookingRepository.search(statusEnum, text, PageRequest.of(page, size));

        return PaginatedResponseDto.<BookingListItemResponseDto>builder()
                .count(result.getTotalElements())
                .dataList(result.getContent().stream().map(bookingMapper::toBookingListItemResponseDto).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDetailResponseDto findById(UUID id) {
        BookingEntity bookingEntity = getBookingOrThrow(id);
        return bookingMapper.toBookingDetailResponseDto(bookingEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerOptionResponseDto> findCustomerOptions(String searchText) {
        String text = searchText == null ? "" : searchText.trim();
        return customerRepository.search(text).stream().map(bookingMapper::toCustomerOptionResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleOptionResponseDto> findAvailableVehicleOptions(String searchText) {
        String text = searchText == null ? "" : searchText.trim();
        return vehicleRepository.searchByStatus(VehicleEntity.Status.AVAILABLE, text)
                .stream().map(bookingMapper::toVehicleOptionResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationOptionResponseDto> findLocationOptions() {
        return locationRepository.findByIsActiveTrueOrderByLocationNameAsc()
                .stream().map(bookingMapper::toLocationOptionResponseDto).toList();
    }

    // ─── Create ─────────────────────────────────────────────────────────────

    @Override
    public UUID create(BookingCreateRequestDto dto) {
        if (!dto.getDropoffDate().isAfter(dto.getPickupDate())) {
            throw new ValidationException("Dropoff date must be after the pickup date");
        }

        CustomerEntity customerEntity = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntryNotFoundException("Customer not found"));

        VehicleEntity vehicleEntity = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new EntryNotFoundException("Vehicle not found"));

        LocationEntity pickupLocationEntity = locationRepository.findById(dto.getPickupLocationId())
                .orElseThrow(() -> new EntryNotFoundException("Pickup location not found"));

        LocationEntity dropoffLocationEntity = locationRepository.findById(dto.getDropoffLocationId())
                .orElseThrow(() -> new EntryNotFoundException("Dropoff location not found"));

        long conflicts = bookingRepository.countConflictingBookings(
                vehicleEntity.getId(), dto.getPickupDate(), dto.getDropoffDate(), null);
        if (conflicts > 0) {
            throw new ValidationException("This vehicle is already booked for the selected dates");
        }

        int totalDays = (int) Math.max(1, ChronoUnit.DAYS.between(dto.getPickupDate(), dto.getDropoffDate()));
        BigDecimal dailyRate = vehicleEntity.getDailyRate();
        BigDecimal totalAmount = dailyRate.multiply(BigDecimal.valueOf(totalDays));
        BigDecimal discount = dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal finalAmount = totalAmount.subtract(discount);

        UserEntity currentUser = getCurrentUserOrNull();

        BookingEntity bookingEntity = BookingEntity.builder()
                .bookingRef(generateBookingRef())
                .status(BookingEntity.BookingStatus.PENDING)
                .pickupDate(dto.getPickupDate())
                .dropoffDate(dto.getDropoffDate())
                .totalDays(totalDays)
                .dailyRate(dailyRate)
                .totalAmount(totalAmount)
                .discountAmount(discount)
                .extraCharges(BigDecimal.ZERO)
                .finalAmount(finalAmount)
                .notes(dto.getNotes())
                .customer(customerEntity)
                .vehicle(vehicleEntity)
                .pickupLocation(pickupLocationEntity)
                .dropoffLocation(dropoffLocationEntity)
                .createdBy(currentUser)
                .build();

        BookingEntity saved = bookingRepository.save(bookingEntity);
        return saved.getId();
    }

    // ─── Status transitions ────────────────────────────────────────────────

    @Override
    public void confirm(UUID id) {
        BookingEntity bookingEntity = getBookingOrThrow(id);

        if (bookingEntity.getStatus() != BookingEntity.BookingStatus.PENDING) {
            throw new ValidationException("Only pending bookings can be confirmed");
        }

        bookingEntity.setStatus(BookingEntity.BookingStatus.CONFIRMED);
        bookingEntity.setApprovedBy(getCurrentUserOrNull());
        bookingRepository.save(bookingEntity);
    }

    @Override
    public void activate(UUID id) {
        BookingEntity bookingEntity = getBookingOrThrow(id);

        if (bookingEntity.getStatus() != BookingEntity.BookingStatus.CONFIRMED) {
            throw new ValidationException("Only confirmed bookings can be activated (vehicle picked up)");
        }

        VehicleEntity vehicleEntity = bookingEntity.getVehicle();
        if (vehicleEntity.getStatus() == VehicleEntity.Status.RENTED) {
            throw new ValidationException("This vehicle is currently marked as rented under another booking");
        }

        bookingEntity.setStatus(BookingEntity.BookingStatus.ACTIVE);
        vehicleEntity.setStatus(VehicleEntity.Status.RENTED);

        bookingRepository.save(bookingEntity);
        vehicleRepository.save(vehicleEntity);
    }

    @Override
    public void complete(UUID id, BookingCompleteRequestDto dto) {
        BookingEntity bookingEntity = getBookingOrThrow(id);

        if (bookingEntity.getStatus() != BookingEntity.BookingStatus.ACTIVE) {
            throw new ValidationException("Only active bookings can be completed (vehicle returned)");
        }

        if (dto.getActualReturnDate().isBefore(bookingEntity.getPickupDate())) {
            throw new ValidationException("Actual return date cannot be before the pickup date");
        }

        BigDecimal extraCharges = dto.getExtraCharges() != null ? dto.getExtraCharges() : BigDecimal.ZERO;
        BigDecimal discount = bookingEntity.getDiscountAmount() != null ? bookingEntity.getDiscountAmount() : BigDecimal.ZERO;

        bookingEntity.setActualReturnDate(dto.getActualReturnDate());
        bookingEntity.setExtraCharges(extraCharges);
        bookingEntity.setExtraChargesNote(dto.getExtraChargesNote());
        bookingEntity.setFinalAmount(bookingEntity.getTotalAmount().subtract(discount).add(extraCharges));
        bookingEntity.setStatus(BookingEntity.BookingStatus.COMPLETED);

        VehicleEntity vehicleEntity = bookingEntity.getVehicle();
        vehicleEntity.setStatus(VehicleEntity.Status.AVAILABLE);
        if (dto.getCurrentMileageKm() != null) {
            vehicleEntity.setCurrentMileageKm(dto.getCurrentMileageKm());
        }

        bookingRepository.save(bookingEntity);
        vehicleRepository.save(vehicleEntity);
    }

    @Override
    public void cancel(UUID id, BookingCancelRequestDto dto) {
        BookingEntity bookingEntity = getBookingOrThrow(id);

        if (bookingEntity.getStatus() != BookingEntity.BookingStatus.PENDING
                && bookingEntity.getStatus() != BookingEntity.BookingStatus.CONFIRMED) {
            throw new ValidationException("Only pending or confirmed bookings can be cancelled");
        }

        bookingEntity.setStatus(BookingEntity.BookingStatus.CANCELLED);
        String note = "Cancelled: " + dto.getReason();
        bookingEntity.setNotes(bookingEntity.getNotes() == null || bookingEntity.getNotes().isBlank()
                ? note
                : bookingEntity.getNotes() + "\n" + note);

        bookingRepository.save(bookingEntity);
    }

    @Override
    public void extend(UUID id, BookingExtensionRequestDto dto) {
        BookingEntity bookingEntity = getBookingOrThrow(id);

        if (bookingEntity.getStatus() != BookingEntity.BookingStatus.ACTIVE) {
            throw new ValidationException("Only active bookings can be extended");
        }

        if (!dto.getNewDropoffDate().isAfter(bookingEntity.getDropoffDate())) {
            throw new ValidationException("New dropoff date must be after the current dropoff date");
        }

        long conflicts = bookingRepository.countConflictingBookings(
                bookingEntity.getVehicle().getId(), bookingEntity.getDropoffDate(), dto.getNewDropoffDate(), bookingEntity.getId());
        if (conflicts > 0) {
            throw new ValidationException("This vehicle already has another booking overlapping the new dates");
        }

        int additionalDays = (int) ChronoUnit.DAYS.between(bookingEntity.getDropoffDate(), dto.getNewDropoffDate());
        BigDecimal additionalAmount = bookingEntity.getDailyRate().multiply(BigDecimal.valueOf(additionalDays));

        UserEntity currentUser = getCurrentUserOrNull();

        BookingExtensionEntity bookingExtensionEntity = bookingEntity.getExtension();
        if (bookingExtensionEntity == null) {
            bookingExtensionEntity = BookingExtensionEntity.builder()
                    .booking(bookingEntity)
                    .build();
        }
        bookingExtensionEntity.setOriginalDropoffDate(bookingEntity.getDropoffDate());
        bookingExtensionEntity.setNewDropoffDate(dto.getNewDropoffDate());
        bookingExtensionEntity.setAdditionalDays(additionalDays);
        bookingExtensionEntity.setAdditionalAmount(additionalAmount);
        bookingExtensionEntity.setReason(dto.getReason());
        bookingExtensionEntity.setStatus(BookingExtensionEntity.ExtensionStatus.APPROVED);
        bookingExtensionEntity.setApprovedBy(currentUser);
        bookingExtensionEntity.setApprovedAt(LocalDateTime.now());

        bookingExtensionRepository.save(bookingExtensionEntity);

        bookingEntity.setDropoffDate(dto.getNewDropoffDate());
        bookingEntity.setTotalDays(bookingEntity.getTotalDays() + additionalDays);
        bookingEntity.setTotalAmount(bookingEntity.getTotalAmount().add(additionalAmount));

        BigDecimal discount = bookingEntity.getDiscountAmount() != null ? bookingEntity.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal extraCharges = bookingEntity.getExtraCharges() != null ? bookingEntity.getExtraCharges() : BigDecimal.ZERO;
        bookingEntity.setFinalAmount(bookingEntity.getTotalAmount().subtract(discount).add(extraCharges));

        bookingRepository.save(bookingEntity);
    }

    @Override
    public void updateNotes(UUID id, BookingNotesRequestDto dto) {
        BookingEntity bookingEntity = getBookingOrThrow(id);
        bookingEntity.setNotes(dto.getNotes());
        bookingRepository.save(bookingEntity);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────
    private BookingEntity getBookingOrThrow(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntryNotFoundException("Booking not found"));
    }

    private BookingEntity.BookingStatus parseStatusOrNull(String status) {
        if (status == null || status.isBlank()) return null;
        try {
            return BookingEntity.BookingStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid booking status: " + status);
        }
    }

    private String generateBookingRef() {
        String ref;
        do {
            ref = "BK" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
                    + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        } while (bookingRepository.existsByBookingRef(ref));
        return ref;
    }

    private UserEntity getCurrentUserOrNull() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) return null;
        return userRepository.findUserEntityByEmail(authentication.getName()).orElse(null);
    }
}
