package com.rentro.service.impl;

import com.rentro.dto.response.dashboard.*;
import com.rentro.entity.BookingEntity;
import com.rentro.entity.PaymentEntity;
import com.rentro.entity.VehicleEntity;
import com.rentro.repository.*;
import com.rentro.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final int RECENT_BOOKINGS_LIMIT = 5;
    private static final int RENTED_VEHICLES_LIMIT = 8;

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final DamageReportRepository damageReportRepository;

    @Override
    public DashboardOverviewResponseDto getOverview() {
        return DashboardOverviewResponseDto.builder()
                .stats(buildStats())
                .recentBookings(buildRecentBookings())
                .vehicleStatus(buildVehicleStatus())
                .build();
    }

    // ─── Stat cards ───────────────────────────────────────────────────────────
    private DashboardStatsResponseDto buildStats() {
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = monthStart.plusMonths(1);

        return DashboardStatsResponseDto.builder()
                .totalBookings(bookingRepository.count())
                .bookingsThisMonth(bookingRepository.countByCreatedAtBetween(monthStart, monthEnd))
                .activeBookings(bookingRepository.countByStatus(BookingEntity.BookingStatus.ACTIVE))

                .totalRevenue(paymentRepository.sumAmountByStatus(PaymentEntity.PaymentStatus.COMPLETED))
                .revenueThisMonth(paymentRepository.sumAmountByStatusAndPaidAtBetween(
                        PaymentEntity.PaymentStatus.COMPLETED, monthStart, monthEnd))

                .totalVehicles(vehicleRepository.count())
                .availableVehicles(vehicleRepository.countByStatus(VehicleEntity.Status.AVAILABLE))

                .totalCustomers(customerRepository.count())
                .newCustomersThisMonth(customerRepository.countByUserCreatedAtBetween(monthStart, monthEnd))

                .pendingPaymentsCount(paymentRepository.countByStatus(PaymentEntity.PaymentStatus.PENDING))
                .pendingPaymentsAmount(paymentRepository.sumAmountByStatus(PaymentEntity.PaymentStatus.PENDING))

                .openDamageReports(damageReportRepository.countByReviewedByIsNull())
                .build();
    }

    // ─── Recent bookings widget ───────────────────────────────────────────────
    private List<RecentBookingResponseDto> buildRecentBookings() {
        return bookingRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .limit(RECENT_BOOKINGS_LIMIT)
                .map(this::toRecentBookingDTO)
                .toList();
    }

    private RecentBookingResponseDto toRecentBookingDTO(BookingEntity bookingEntity) {
        VehicleEntity vehicleEntity = bookingEntity.getVehicle();

        return RecentBookingResponseDto.builder()
                .id(bookingEntity.getId())
                .bookingRef(bookingEntity.getBookingRef())
                .customerName(bookingEntity.getCustomer().getUser().getFullName())
                .vehicleName(vehicleEntity.getMake() + " " + vehicleEntity.getModel() + " - " + vehicleEntity.getRegNo())
                .status(bookingEntity.getStatus())
                .pickupDate(bookingEntity.getPickupDate())
                .dropoffDate(bookingEntity.getDropoffDate())
                .totalAmount(bookingEntity.getTotalAmount())
                .createdAt(bookingEntity.getCreatedAt())
                .build();
    }

    // ─── Vehicle status widget ────────────────────────────────────────────────
    private VehicleStatusResponseDto buildVehicleStatus() {
        return VehicleStatusResponseDto.builder()
                .total(vehicleRepository.count())
                .available(vehicleRepository.countByStatus(VehicleEntity.Status.AVAILABLE))
                .rented(vehicleRepository.countByStatus(VehicleEntity.Status.RENTED))
                .maintenance(vehicleRepository.countByStatus(VehicleEntity.Status.MAINTENANCE))
                .inactive(vehicleRepository.countByStatus(VehicleEntity.Status.INACTIVE))
                .rentedVehicles(buildRentedVehicles())
                .build();
    }

    private List<RentedVehicleResponseDto> buildRentedVehicles() {
        return bookingRepository.findByStatusOrderByDropoffDateAsc(BookingEntity.BookingStatus.ACTIVE)
                .stream()
                .limit(RENTED_VEHICLES_LIMIT)
                .map(this::toRentedVehicleDTO)
                .toList();
    }

    private RentedVehicleResponseDto toRentedVehicleDTO(BookingEntity bookingEntity) {
        VehicleEntity vehicleEntity = bookingEntity.getVehicle();

        return RentedVehicleResponseDto.builder()
                .vehicleId(vehicleEntity.getId())
                .vehicleName(vehicleEntity.getMake() + " " + vehicleEntity.getModel())
                .regNo(vehicleEntity.getRegNo())
                .bookingRef(bookingEntity.getBookingRef())
                .customerName(bookingEntity.getCustomer().getUser().getFullName())
                .dropoffDate(bookingEntity.getDropoffDate())
                .build();
    }
}
