package com.rentro.repository;

import com.rentro.entity.VehicleDamageImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleDamageImageRepository extends JpaRepository<VehicleDamageImageEntity, Integer> {

    List<VehicleDamageImageEntity> findByDamageId(Integer damageId);
}
