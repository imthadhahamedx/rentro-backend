package com.rentro.service.impl;

import com.rentro.dto.response.report.*;
import com.rentro.entity.BookingEntity;
import com.rentro.entity.DamageEntity;
import com.rentro.entity.PaymentEntity;
import com.rentro.entity.VehicleEntity;
import com.rentro.repository.BookingRepository;
import com.rentro.repository.DamageRepository;
import com.rentro.repository.PaymentRepository;
import com.rentro.repository.VehicleRepository;
import com.rentro.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy");
    private static final int TOP_LIST_LIMIT = 10;

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final VehicleRepository vehicleRepository;
    private final DamageRepository damageRepository;

    // ─── Revenue report ─────────────────────────────────────────────────────────
    @Override
    public RevenueReportResponseDto getRevenueReport(LocalDate startDate, LocalDate endDate) {
        LocalDate start = resolveStart(startDate, endDate);
        LocalDate end = resolveEnd(endDate);
        LocalDateTime startDT = start.atStartOfDay();
        LocalDateTime endDT = end.plusDays(1).atStartOfDay();

        List<PaymentEntity> payments = paymentRepository.findCompletedForReport(
                PaymentEntity.PaymentStatus.COMPLETED, startDT, endDT);

        BigDecimal totalRevenue = sumAmounts(payments, PaymentEntity::getAmount);
        long totalPayments = payments.size();
        BigDecimal avgPayment = totalPayments == 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(totalPayments), 2, RoundingMode.HALF_UP);

        Map<YearMonth, List<PaymentEntity>> byMonth = payments.stream()
                .filter(p -> p.getPaidAt() != null)
                .collect(Collectors.groupingBy(p -> YearMonth.from(p.getPaidAt())));

        List<MonthlyPointResponseDto> monthlyRevenue = monthRange(start, end).stream()
                .map(ym -> {
                    List<PaymentEntity> monthPayments = byMonth.getOrDefault(ym, List.of());
                    return MonthlyPointResponseDto.builder()
                            .month(ym.format(MONTH_FORMATTER))
                            .count(monthPayments.size())
                            .amount(sumAmounts(monthPayments, PaymentEntity::getAmount))
                            .build();
                })
                .toList();

        List<LabeledAmountResponseDto> byMethod = payments.stream()
                .collect(Collectors.groupingBy(PaymentEntity::getPaymentMethod))
                .entrySet().stream()
                .map(e -> LabeledAmountResponseDto.builder()
                        .label(e.getKey().name())
                        .amount(sumAmounts(e.getValue(), PaymentEntity::getAmount))
                        .count(e.getValue().size())
                        .build())
                .sorted(Comparator.comparing(LabeledAmountResponseDto::getAmount).reversed())
                .toList();

        List<LabeledAmountResponseDto> byType = payments.stream()
                .collect(Collectors.groupingBy(PaymentEntity::getPaymentType))
                .entrySet().stream()
                .map(e -> LabeledAmountResponseDto.builder()
                        .label(e.getKey().name())
                        .amount(sumAmounts(e.getValue(), PaymentEntity::getAmount))
                        .count(e.getValue().size())
                        .build())
                .sorted(Comparator.comparing(LabeledAmountResponseDto::getAmount).reversed())
                .toList();

        Map<VehicleEntity, List<PaymentEntity>> byVehicle = payments.stream()
                .filter(p -> p.getBooking() != null && p.getBooking().getVehicle() != null)
                .collect(Collectors.groupingBy(p -> p.getBooking().getVehicle()));

        List<TopVehicleRevenueResponseDto> topVehicles = byVehicle.entrySet().stream()
                .map(e -> {
                    VehicleEntity v = e.getKey();
                    long bookingsCount = e.getValue().stream()
                            .map(p -> p.getBooking().getId())
                            .distinct()
                            .count();
                    return TopVehicleRevenueResponseDto.builder()
                            .vehicleId(v.getId().toString())
                            .vehicleName(v.getMake() + " " + v.getModel())
                            .regNo(v.getRegNo())
                            .bookingsCount(bookingsCount)
                            .revenue(sumAmounts(e.getValue(), PaymentEntity::getAmount))
                            .build();
                })
                .sorted(Comparator.comparing(TopVehicleRevenueResponseDto::getRevenue).reversed())
                .limit(TOP_LIST_LIMIT)
                .toList();

        return RevenueReportResponseDto.builder()
                .startDate(start.toString())
                .endDate(end.toString())
                .totalRevenue(totalRevenue)
                .totalPayments(totalPayments)
                .averagePaymentAmount(avgPayment)
                .monthlyRevenue(monthlyRevenue)
                .byPaymentMethod(byMethod)
                .byPaymentType(byType)
                .topVehicles(topVehicles)
                .build();
    }

    // ─── Bookings report ────────────────────────────────────────────────────────
    @Override
    public BookingsReportResponseDto getBookingsReport(LocalDate startDate, LocalDate endDate) {
        LocalDate start = resolveStart(startDate, endDate);
        LocalDate end = resolveEnd(endDate);
        LocalDateTime startDT = start.atStartOfDay();
        LocalDateTime endDT = end.plusDays(1).atStartOfDay();

        List<BookingEntity> bookings = bookingRepository.findAllForReport(startDT, endDT);

        long totalBookings = bookings.size();
        BigDecimal totalRevenue = sumAmounts(bookings, this::effectiveBookingAmount);
        BigDecimal avgBookingValue = totalBookings == 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP);
        double avgDuration = bookings.stream()
                .filter(b -> b.getTotalDays() != null)
                .mapToInt(BookingEntity::getTotalDays)
                .average()
                .orElse(0);

        List<LabeledCountResponseDto> byStatus = bookings.stream()
                .collect(Collectors.groupingBy(BookingEntity::getStatus))
                .entrySet().stream()
                .map(e -> LabeledCountResponseDto.builder().label(e.getKey().name()).count(e.getValue().size()).build())
                .sorted(Comparator.comparingLong(LabeledCountResponseDto::getCount).reversed())
                .toList();

        Map<YearMonth, List<BookingEntity>> byMonth = bookings.stream()
                .filter(b -> b.getCreatedAt() != null)
                .collect(Collectors.groupingBy(b -> YearMonth.from(b.getCreatedAt())));

        List<MonthlyPointResponseDto> monthlyBookings = monthRange(start, end).stream()
                .map(ym -> {
                    List<BookingEntity> monthBookings = byMonth.getOrDefault(ym, List.of());
                    return MonthlyPointResponseDto.builder()
                            .month(ym.format(MONTH_FORMATTER))
                            .count(monthBookings.size())
                            .amount(sumAmounts(monthBookings, this::effectiveBookingAmount))
                            .build();
                })
                .toList();

        List<LabeledCountResponseDto> byLocation = bookings.stream()
                .filter(b -> b.getPickupLocation() != null)
                .collect(Collectors.groupingBy(b -> b.getPickupLocation().getLocationName()))
                .entrySet().stream()
                .map(e -> LabeledCountResponseDto.builder().label(e.getKey()).count(e.getValue().size()).build())
                .sorted(Comparator.comparingLong(LabeledCountResponseDto::getCount).reversed())
                .toList();

        Map<UUID, List<BookingEntity>> byCustomer = bookings.stream()
                .filter(b -> b.getCustomer() != null)
                .collect(Collectors.groupingBy(b -> b.getCustomer().getId()));

        List<TopCustomerResponseDto> topCustomers = byCustomer.values().stream()
                .map(list -> {
                    BookingEntity sample = list.get(0);
                    return TopCustomerResponseDto.builder()
                            .customerId(sample.getCustomer().getId().toString())
                            .customerName(sample.getCustomer().getUser().getFullName())
                            .bookingsCount(list.size())
                            .totalSpent(sumAmounts(list, this::effectiveBookingAmount))
                            .build();
                })
                .sorted(Comparator.comparing(TopCustomerResponseDto::getTotalSpent).reversed())
                .limit(TOP_LIST_LIMIT)
                .toList();

        return BookingsReportResponseDto.builder()
                .startDate(start.toString())
                .endDate(end.toString())
                .totalBookings(totalBookings)
                .totalRevenue(totalRevenue)
                .averageBookingValue(avgBookingValue)
                .averageDurationDays(avgDuration)
                .byStatus(byStatus)
                .monthlyBookings(monthlyBookings)
                .byPickupLocation(byLocation)
                .topCustomers(topCustomers)
                .build();
    }

    // ─── Vehicles report ────────────────────────────────────────────────────────
    @Override
    public VehiclesReportResponseDto getVehiclesReport(LocalDate startDate, LocalDate endDate) {
        LocalDate start = resolveStart(startDate, endDate);
        LocalDate end = resolveEnd(endDate);
        LocalDateTime startDT = start.atStartOfDay();
        LocalDateTime endDT = end.plusDays(1).atStartOfDay();
        long periodDays = Math.max(1, ChronoUnit.DAYS.between(start, end) + 1);

        List<VehicleEntity> vehicles = vehicleRepository.findAllWithCategory();
        List<BookingEntity> bookings = bookingRepository.findAllForReport(startDT, endDT);

        Map<UUID, List<BookingEntity>> bookingsByVehicle = bookings.stream()
                .filter(b -> b.getVehicle() != null)
                .collect(Collectors.groupingBy(b -> b.getVehicle().getId()));

        List<LabeledCountResponseDto> byStatus = vehicles.stream()
                .collect(Collectors.groupingBy(VehicleEntity::getStatus))
                .entrySet().stream()
                .map(e -> LabeledCountResponseDto.builder().label(e.getKey().name()).count(e.getValue().size()).build())
                .sorted(Comparator.comparingLong(LabeledCountResponseDto::getCount).reversed())
                .toList();

        List<LabeledCountResponseDto> byCategory = vehicles.stream()
                .filter(v -> v.getVehicleCategory() != null)
                .collect(Collectors.groupingBy(v -> v.getVehicleCategory().getCategory()))
                .entrySet().stream()
                .map(e -> LabeledCountResponseDto.builder().label(e.getKey()).count(e.getValue().size()).build())
                .sorted(Comparator.comparingLong(LabeledCountResponseDto::getCount).reversed())
                .toList();

        List<VehicleUtilizationResponseDto> utilization = vehicles.stream()
                .map(v -> {
                    List<BookingEntity> vBookings = bookingsByVehicle.getOrDefault(v.getId(), List.of());
                    long daysBooked = vBookings.stream()
                            .filter(b -> b.getTotalDays() != null)
                            .mapToLong(BookingEntity::getTotalDays)
                            .sum();
                    BigDecimal revenue = sumAmounts(vBookings, this::effectiveBookingAmount);
                    double utilizationRate = Math.min(100.0, (daysBooked * 100.0) / periodDays);

                    return VehicleUtilizationResponseDto.builder()
                            .vehicleId(v.getId().toString())
                            .vehicleName(v.getMake() + " " + v.getModel())
                            .regNo(v.getRegNo())
                            .categoryName(v.getVehicleCategory() != null ? v.getVehicleCategory().getCategory() : "—")
                            .status(v.getStatus() != null ? v.getStatus().name() : "—")
                            .bookingsCount(vBookings.size())
                            .daysBooked(daysBooked)
                            .revenue(revenue)
                            .utilizationRate(Math.round(utilizationRate * 10.0) / 10.0)
                            .build();
                })
                .sorted(Comparator.comparing(VehicleUtilizationResponseDto::getRevenue).reversed())
                .toList();

        return VehiclesReportResponseDto.builder()
                .startDate(start.toString())
                .endDate(end.toString())
                .totalVehicles(vehicles.size())
                .byStatus(byStatus)
                .byCategory(byCategory)
                .utilization(utilization)
                .build();
    }

    // ─── Damage report ──────────────────────────────────────────────────────────
    @Override
    public DamageReportSummaryResponseDto getDamageReport(LocalDate startDate, LocalDate endDate) {
        LocalDate start = resolveStart(startDate, endDate);
        LocalDate end = resolveEnd(endDate);
        LocalDateTime startDT = start.atStartOfDay();
        LocalDateTime endDT = end.plusDays(1).atStartOfDay();

        List<DamageEntity> damages = damageRepository.findAllForReport(startDT, endDT);

        long total = damages.size();
        long fixed = damages.stream().filter(d -> Boolean.TRUE.equals(d.getIsFixed())).count();
        long unfixed = total - fixed;

        List<LabeledCountResponseDto> byDamageBy = damages.stream()
                .filter(d -> d.getDamageBy() != null)
                .collect(Collectors.groupingBy(DamageEntity::getDamageBy))
                .entrySet().stream()
                .map(e -> LabeledCountResponseDto.builder().label(e.getKey().name()).count(e.getValue().size()).build())
                .sorted(Comparator.comparingLong(LabeledCountResponseDto::getCount).reversed())
                .toList();

        Map<YearMonth, List<DamageEntity>> byMonth = damages.stream()
                .filter(d -> d.getCreatedAt() != null)
                .collect(Collectors.groupingBy(d -> YearMonth.from(d.getCreatedAt())));

        List<MonthlyPointResponseDto> monthlyDamages = monthRange(start, end).stream()
                .map(ym -> MonthlyPointResponseDto.builder()
                        .month(ym.format(MONTH_FORMATTER))
                        .count(byMonth.getOrDefault(ym, List.of()).size())
                        .amount(BigDecimal.ZERO)
                        .build())
                .toList();

        Map<UUID, List<DamageEntity>> byVehicle = damages.stream()
                .filter(d -> d.getVehicle() != null)
                .collect(Collectors.groupingBy(d -> d.getVehicle().getId()));

        List<VehicleDamageCountResponseDto> topDamagedVehicles = byVehicle.values().stream()
                .map(list -> {
                    VehicleEntity v = list.get(0).getVehicle();
                    long unfixedForVehicle = list.stream()
                            .filter(d -> !Boolean.TRUE.equals(d.getIsFixed()))
                            .count();
                    return VehicleDamageCountResponseDto.builder()
                            .vehicleId(v.getId().toString())
                            .vehicleName(v.getMake() + " " + v.getModel())
                            .regNo(v.getRegNo())
                            .damageCount(list.size())
                            .unfixedCount(unfixedForVehicle)
                            .build();
                })
                .sorted(Comparator.comparingLong(VehicleDamageCountResponseDto::getDamageCount).reversed())
                .limit(TOP_LIST_LIMIT)
                .toList();

        return DamageReportSummaryResponseDto.builder()
                .startDate(start.toString())
                .endDate(end.toString())
                .totalDamages(total)
                .fixedCount(fixed)
                .unfixedCount(unfixed)
                .byDamageBy(byDamageBy)
                .monthlyDamages(monthlyDamages)
                .topDamagedVehicles(topDamagedVehicles)
                .build();
    }

    // ─── Shared helpers ─────────────────────────────────────────────────────────
    /** Defaults to 12 months before `end` (start of that month) when not supplied. */
    private LocalDate resolveStart(LocalDate startDate, LocalDate endDate) {
        if (startDate != null) return startDate;
        LocalDate end = resolveEnd(endDate);
        return end.minusMonths(11).withDayOfMonth(1);
    }

    private LocalDate resolveEnd(LocalDate endDate) {
        return endDate != null ? endDate : LocalDate.now();
    }

    private List<YearMonth> monthRange(LocalDate start, LocalDate end) {
        List<YearMonth> months = new ArrayList<>();
        YearMonth cursor = YearMonth.from(start);
        YearMonth last = YearMonth.from(end);
        while (!cursor.isAfter(last)) {
            months.add(cursor);
            cursor = cursor.plusMonths(1);
        }
        return months;
    }

    private <T> BigDecimal sumAmounts(List<T> items, java.util.function.Function<T, BigDecimal> extractor) {
        return items.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal effectiveBookingAmount(BookingEntity bookingEntity) {
        BigDecimal amount = bookingEntity.getFinalAmount() != null ? bookingEntity.getFinalAmount() : bookingEntity.getTotalAmount();
        return amount != null ? amount : BigDecimal.ZERO;
    }
}
