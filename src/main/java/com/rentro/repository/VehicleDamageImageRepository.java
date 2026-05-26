package com.rentro.repository;

import com.rentro.entity.VehicleDamageImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VehicleDamageImageRepository extends JpaRepository<VehicleDamageImageEntity, UUID> {

}
