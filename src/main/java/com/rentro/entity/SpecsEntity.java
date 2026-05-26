package com.rentro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "specs")
@Builder
public class SpecsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "property_id")
    private UUID id;

    @Column(name = "specification", columnDefinition = "TEXT")
    private String specification;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "specs_has_vehicle",
            joinColumns = @JoinColumn(name = "specs_property_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_property_id")
    )
    private List<VehicleEntity> vehicles;
}
