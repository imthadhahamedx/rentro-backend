package com.rentro.repository;

import com.rentro.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, UUID> {

    List<LocationEntity> findByIsActiveTrueOrderByLocationNameAsc();

}
