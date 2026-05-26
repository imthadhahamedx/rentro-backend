package com.rentro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "vehicle")
@Builder
public class VehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "property_id")
    private UUID id;

    @Column(name = "make", length = 45)
    private String make;

    @Column(name = "model", length = 45)
    private String model;

    @Column(name = "model_year", columnDefinition = "YEAR")
    private Integer modelYear;

    @Column(name = "reg_no", length = 45)
    private String regNo;

    @Column(name = "colour", length = 45)
    private String colour;

    @Enumerated(EnumType.STRING)
    @Column(name = "transmission")
    private Transmission transmission;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private FuelType fuelType;

    @Column(name = "seat_count")
    private Integer seatCount;

    @Column(name = "door_count")
    private Integer doorCount;

    @Column(name = "daily_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "current_mileage_km")
    private Integer currentMileageKm;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_category_property_id", nullable = false)
    private VehicleCategoryEntity vehicleCategory;

    @ManyToMany(mappedBy = "vehicles", fetch = FetchType.LAZY)
    private List<SpecsEntity> specs;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VehicleImageEntity> vehicleImages;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DamageEntity> damages;

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private List<BookingEntity> bookings;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Transmission {
        MANUAL, AUTOMATIC, TIPTRONIC
    }

    public enum FuelType {
        PETROL, DIESEL, HYBRID, ELECTRIC
    }

    public enum Status {
        AVAILABLE, RENTED, MAINTENANCE, INACTIVE
    }
}
