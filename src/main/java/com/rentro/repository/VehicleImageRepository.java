package com.rentro.repository;

import com.rentro.entity.VehicleImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleImageRepository extends JpaRepository<VehicleImageEntity, Integer> {

    List<VehicleImageEntity> findByVehicleId(Integer vehicleId);
}
