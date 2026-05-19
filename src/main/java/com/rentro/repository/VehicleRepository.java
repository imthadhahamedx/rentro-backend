package com.rentro.repository;

import com.rentro.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Integer> {

    Optional<VehicleEntity> findByRegNo(String regNo);
    List<VehicleEntity> findByStatus(VehicleEntity.Status status);
    List<VehicleEntity> findByVehicleCategoryId(Integer categoryId);
    List<VehicleEntity> findByFuelType(VehicleEntity.FuelType fuelType);
    List<VehicleEntity> findByTransmission(VehicleEntity.Transmission transmission);
}
