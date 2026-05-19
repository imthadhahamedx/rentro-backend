package com.rentro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "damage")
@Builder
public class DamageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Integer id;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_fixed")
    private Boolean isFixed;

    @Enumerated(EnumType.STRING)
    @Column(name = "damage_by")
    private DamageBy damageBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "fixed_at")
    private LocalDateTime fixedAt;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_property_id", nullable = false)
    private VehicleEntity vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by", nullable = false)
    private UserEntity markedBy;

    @OneToMany(mappedBy = "damage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VehicleDamageImageEntity> damageImages;

    @OneToMany(mappedBy = "damage", fetch = FetchType.LAZY)
    private List<DamageReportEntity> damageReports;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum DamageBy {
        CUSTOMER, INTERNAL, OTHER
    }
}
