package com.rentro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "booking_extension")
@Builder
public class BookingExtensionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Integer id;

    @Column(name = "original_dropoff_date", nullable = false)
    private LocalDate originalDropoffDate;

    @Column(name = "new_dropoff_date", nullable = false)
    private LocalDate newDropoffDate;

    @Column(name = "additional_days", nullable = false)
    private Integer additionalDays;

    @Column(name = "additional_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal additionalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExtensionStatus status = ExtensionStatus.PENDING;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_property_id", nullable = false)
    private BookingEntity booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private UserEntity approvedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ExtensionStatus {
        PENDING, APPROVED, REJECTED
    }
}
