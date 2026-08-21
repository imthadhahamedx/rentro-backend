package com.rentro.repository;

import com.rentro.entity.LocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, UUID> {

    List<LocationEntity> findByIsActiveTrueOrderByLocationNameAsc();

    // ─── Admin list: search + optional active filter ──────────────────────────
    @Query("SELECT l FROM LocationEntity l " +
            "WHERE (:isActive IS NULL OR l.isActive = :isActive) " +
            "AND (:searchText = '' " +
            "     OR LOWER(l.locationName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(l.city) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(l.address) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
            "ORDER BY l.locationName ASC")
    Page<LocationEntity> search(
            @Param("searchText") String searchText,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    boolean existsByLocationNameIgnoreCaseAndCityIgnoreCase(String locationName, String city);

}
