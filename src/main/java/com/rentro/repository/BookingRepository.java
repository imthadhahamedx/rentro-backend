package com.rentro.repository;

import com.rentro.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {

    Optional<BookingEntity> findByBookingRef(String bookingRef);
    List<BookingEntity> findByCustomerId(Integer customerId);
    List<BookingEntity> findByVehicleId(Integer vehicleId);
    List<BookingEntity> findByStatus(BookingEntity.BookingStatus status);
    List<BookingEntity> findByCustomerIdAndStatus(Integer customerId, BookingEntity.BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.vehicle.id = :vehicleId " +
            "AND b.status NOT IN ('CANCELLED', 'COMPLETED', 'NO_SHOW') " +
            "AND (b.pickupDate <= :dropoff AND b.dropoffDate >= :pickup)")
    List<BookingEntity> findConflictingBookings(
            @Param("vehicleId") Integer vehicleId,
            @Param("pickup") LocalDate pickupDate,
            @Param("dropoff") LocalDate dropoffDate
    );
}
