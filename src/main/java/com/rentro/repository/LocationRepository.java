package com.rentro.repository;

import com.rentro.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Integer> {

    List<LocationEntity> findByIsActive(Boolean isActive);
    List<LocationEntity> findByCityIgnoreCase(String city);
}
