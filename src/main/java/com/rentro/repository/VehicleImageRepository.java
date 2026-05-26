package com.rentro.repository;

import com.rentro.entity.VehicleImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VehicleImageRepository extends JpaRepository<VehicleImageEntity, UUID> {

}
