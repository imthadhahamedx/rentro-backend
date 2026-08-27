package com.rentro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stores walk-in / website visitor booking requests before a customer
 * account exists.  Staff review these and either create a full Booking
 * or contact the visitor directly.
 */
@Entity
@Table(name = "public_booking_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicBookingRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Human-readable reference shown to the visitor. */
    @Column(name = "booking_ref", nullable = false, unique = true, length = 30)
    private String bookingRef;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @Column(name = "pickup_location", nullable = false, length = 200)
    private String pickupLocation;

    @Column(name = "pickup_date", nullable = false)
    private LocalDate pickupDate;

    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @Column(name = "total_days", nullable = false)
    private int totalDays;

    @Column(name = "estimated_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedAmount;

    /** FK to Vehicle (nullable — vehicle could be deleted later). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicle;

    /** Snapshot of vehicle name at request time, in case the vehicle is removed. */
    @Column(name = "vehicle_name", nullable = false, length = 200)
    private String vehicleName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum RequestStatus {
        PENDING, REVIEWED, CONVERTED, REJECTED
    }
}
