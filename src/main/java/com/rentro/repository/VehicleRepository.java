package com.rentro.repository;

import com.rentro.entity.VehicleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, UUID> {

    long countByStatus(VehicleEntity.Status status);

    @Query("SELECT v FROM VehicleEntity v WHERE v.status = :status " +
            "AND (:searchText = '' " +
            "     OR LOWER(v.make) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(v.model) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(v.regNo) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
            "ORDER BY v.make ASC, v.model ASC")
    List<VehicleEntity> searchByStatus(@Param("status") VehicleEntity.Status status, @Param("searchText") String searchText);

    boolean existsByRegNoIgnoreCase(String regNo);

    boolean existsByRegNoIgnoreCaseAndIdNot(String regNo, UUID id);

    @Query("SELECT v FROM VehicleEntity v LEFT JOIN FETCH v.vehicleCategory " +
            "WHERE (:searchText = '' " +
            "     OR LOWER(v.make) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(v.model) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(v.regNo) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
            "AND (:categoryId IS NULL OR v.vehicleCategory.id = :categoryId) " +
            "AND (:status IS NULL OR v.status = :status) " +
            "ORDER BY v.createdAt DESC")
    Page<VehicleEntity> search(
            @Param("searchText") String searchText,
            @Param("categoryId") UUID categoryId,
            @Param("status") VehicleEntity.Status status,
            Pageable pageable
    );

    @Query("SELECT v FROM VehicleEntity v LEFT JOIN FETCH v.vehicleCategory LEFT JOIN FETCH v.vehicleImages " +
            "WHERE v.id = :id")
    Optional<VehicleEntity> findByIdWithDetails(@Param("id") UUID id);

    // ─── Reports: full fleet with category, for utilization breakdowns ────────
    @Query("SELECT v FROM VehicleEntity v LEFT JOIN FETCH v.vehicleCategory")
    List<VehicleEntity> findAllWithCategory();
}
