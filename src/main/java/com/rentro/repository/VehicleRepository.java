package com.rentro.repository;

import com.rentro.entity.VehicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, UUID> {

    @Query(value = "SELECT * FROM vehicle WHERE make LIKE %?1% OR model LIKE %?1% OR reg_no LIKE %?1% OR colour LIKE %?1%", nativeQuery = true)
    public Page<VehicleEntity> findAllVehicleEntity(String searchText, Pageable pageable);
    @Query(value = "SELECT COUNT(*) FROM vehicle WHERE make LIKE %?1% OR model LIKE %?1% OR reg_no LIKE %?1% OR colour LIKE %?1%", nativeQuery = true)
    public Long findAllCount(String searchText);

}
