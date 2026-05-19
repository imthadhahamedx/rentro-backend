package com.rentro.repository;

import com.rentro.entity.VehicleCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleCategoryRepository extends JpaRepository<VehicleCategoryEntity, Integer> {

    Optional<VehicleCategoryEntity> findByCategoryIgnoreCase(String category);
}
