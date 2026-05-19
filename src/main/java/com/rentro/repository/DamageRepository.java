package com.rentro.repository;

import com.rentro.entity.DamageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DamageRepository extends JpaRepository<DamageEntity, Integer> {

    List<DamageEntity> findByVehicleId(Integer vehicleId);
    List<DamageEntity> findByIsFixed(Boolean isFixed);
    List<DamageEntity> findByVehicleIdAndIsFixed(Integer vehicleId, Boolean isFixed);
}
