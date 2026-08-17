package com.rentro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "vehicle_image")
@Builder
public class VehicleImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "property_id")
    private UUID id;

    /** Original file name as uploaded by the client. */
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    /** S3 object key, e.g. "vehicles/{vehicleId}/{uuid}.jpg" - used to delete/replace the object. */
    @Column(name = "directory", nullable = false, length = 512)
    private String directory;

    /** Public S3 URL used by clients to render the image. */
    @Column(name = "resource_url", nullable = false, length = 1024)
    private String resourceUrl;

    /** Content-type of the stored file, e.g. "image/jpeg". */
    @Column(name = "hash", length = 100)
    private String hash;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_property_id", nullable = false)
    private VehicleEntity vehicle;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isPrimary == null) {
            isPrimary = false;
        }
    }
}
