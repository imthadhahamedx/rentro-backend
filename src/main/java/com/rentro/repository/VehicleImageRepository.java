package com.rentro.repository;

import com.rentro.entity.VehicleImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleImageRepository extends JpaRepository<VehicleImageEntity, UUID> {

    List<VehicleImageEntity> findByVehicleId(UUID vehicleId);

    long countByVehicleId(UUID vehicleId);
}
