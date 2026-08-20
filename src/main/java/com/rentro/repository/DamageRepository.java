package com.rentro.repository;

import com.rentro.entity.DamageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DamageRepository extends JpaRepository<DamageEntity, UUID> {

    @Query("SELECT d FROM DamageEntity d LEFT JOIN FETCH d.vehicle v LEFT JOIN FETCH d.markedBy " +
            "WHERE (:searchText = '' " +
            "     OR LOWER(v.make) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(v.model) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(v.regNo) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(d.description) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
            "AND (:isFixed IS NULL OR d.isFixed = :isFixed) " +
            "AND (:damageBy IS NULL OR d.damageBy = :damageBy) " +
            "AND (:vehicleId IS NULL OR v.id = :vehicleId) " +
            "ORDER BY d.createdAt DESC")
    Page<DamageEntity> search(
            @Param("searchText") String searchText,
            @Param("isFixed") Boolean isFixed,
            @Param("damageBy") DamageEntity.DamageBy damageBy,
            @Param("vehicleId") UUID vehicleId,
            Pageable pageable
    );

    @Query("SELECT d FROM DamageEntity d LEFT JOIN FETCH d.vehicle LEFT JOIN FETCH d.markedBy LEFT JOIN FETCH d.damageImages " +
            "WHERE d.id = :id")
    Optional<DamageEntity> findByIdWithDetails(@Param("id") UUID id);

    long countByIsFixedFalse();

    // ─── Reports: all damages logged within a period, fully hydrated ──────────
    @Query("SELECT d FROM DamageEntity d LEFT JOIN FETCH d.vehicle " +
            "WHERE d.createdAt BETWEEN :start AND :end " +
            "ORDER BY d.createdAt DESC")
    List<DamageEntity> findAllForReport(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
