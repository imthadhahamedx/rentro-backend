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
@Table(name = "location")
@Builder
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "property_id")
    private UUID id;

    @Column(name = "location_name", nullable = false, length = 45)
    private String locationName;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", nullable = false, length = 45)
    private String city;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "pickupLocation", fetch = FetchType.LAZY)
    private List<BookingEntity> pickupBookings;

    @OneToMany(mappedBy = "dropoffLocation", fetch = FetchType.LAZY)
    private List<BookingEntity> dropoffBookings;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
