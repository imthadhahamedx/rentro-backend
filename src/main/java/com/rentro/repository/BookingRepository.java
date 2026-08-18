package com.rentro.repository;

import com.rentro.entity.BookingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {

    long countByStatus(BookingEntity.BookingStatus status);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<BookingEntity> findTop5ByOrderByCreatedAtDesc();

    // "Currently rented" = active bookings, soonest due back first
    List<BookingEntity> findByStatusOrderByDropoffDateAsc(BookingEntity.BookingStatus status);

    boolean existsByBookingRef(String bookingRef);

    // ─── Booking list (search + optional status filter) ───────────────────────
    @Query("SELECT b FROM BookingEntity b " +
            "JOIN b.customer c JOIN c.user u " +
            "JOIN b.vehicle v " +
            "WHERE (:status IS NULL OR b.status = :status) " +
            "AND (:searchText = '' " +
            "     OR LOWER(b.bookingRef) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(v.regNo) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(v.make) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(v.model) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
            "ORDER BY b.createdAt DESC")
    Page<BookingEntity> search(
            @Param("status") BookingEntity.BookingStatus status,
            @Param("searchText") String searchText,
            Pageable pageable
    );

    // ─── Overlap check: does this vehicle already have a live booking in this date range? ───
    @Query("SELECT COUNT(b) FROM BookingEntity b " +
            "WHERE b.vehicle.id = :vehicleId " +
            "AND b.status IN ('PENDING', 'CONFIRMED', 'ACTIVE') " +
            "AND b.pickupDate < :dropoffDate AND b.dropoffDate > :pickupDate " +
            "AND (:excludeBookingId IS NULL OR b.id <> :excludeBookingId)")
    long countConflictingBookings(
            @Param("vehicleId") UUID vehicleId,
            @Param("pickupDate") LocalDate pickupDate,
            @Param("dropoffDate") LocalDate dropoffDate,
            @Param("excludeBookingId") UUID excludeBookingId
    );

    // ─── Customer management: booking history for a single customer ───────────
    long countByCustomerId(UUID customerId);

    @Query("SELECT b FROM BookingEntity b JOIN b.vehicle v " +
            "WHERE b.customer.id = :customerId " +
            "AND (:status IS NULL OR b.status = :status) " +
            "ORDER BY b.createdAt DESC")
    Page<BookingEntity> searchByCustomer(
            @Param("customerId") UUID customerId,
            @Param("status") BookingEntity.BookingStatus status,
            Pageable pageable
    );

    // ─── Location management: booking counts / guard for deletion ─────────────
    long countByPickupLocationId(UUID locationId);

    long countByDropoffLocationId(UUID locationId);

    // ─── Reports: all bookings created within a period, fully hydrated ────────
    @Query("SELECT b FROM BookingEntity b " +
            "JOIN FETCH b.customer c JOIN FETCH c.user " +
            "JOIN FETCH b.vehicle v " +
            "LEFT JOIN FETCH b.pickupLocation " +
            "WHERE b.createdAt BETWEEN :start AND :end " +
            "ORDER BY b.createdAt DESC")
    List<BookingEntity> findAllForReport(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
