package com.rentro.repository;

import com.rentro.entity.DamageReportEntity;
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
public interface DamageReportRepository extends JpaRepository<DamageReportEntity, UUID> {

    // Not yet reviewed by a staff member = "open"
    long countByReviewedByIsNull();

    @Query("SELECT r FROM DamageReportEntity r " +
            "LEFT JOIN FETCH r.booking b " +
            "LEFT JOIN FETCH b.customer c " +
            "LEFT JOIN FETCH r.damage d " +
            "LEFT JOIN FETCH d.vehicle v " +
            "LEFT JOIN FETCH r.reviewedBy " +
            "WHERE (:searchText = '' " +
            "     OR LOWER(b.bookingRef) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(v.regNo) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
            "AND (:reviewed IS NULL " +
            "     OR (:reviewed = true AND r.reviewedBy IS NOT NULL) " +
            "     OR (:reviewed = false AND r.reviewedBy IS NULL)) " +
            "ORDER BY r.date DESC")
    Page<DamageReportEntity> search(
            @Param("searchText") String searchText,
            @Param("reviewed") Boolean reviewed,
            Pageable pageable
    );

    @Query("SELECT r FROM DamageReportEntity r " +
            "LEFT JOIN FETCH r.booking b " +
            "LEFT JOIN FETCH b.customer c " +
            "LEFT JOIN FETCH r.damage d " +
            "LEFT JOIN FETCH d.vehicle v " +
            "LEFT JOIN FETCH d.damageImages " +
            "LEFT JOIN FETCH r.reviewedBy " +
            "WHERE r.id = :id")
    Optional<DamageReportEntity> findByIdWithDetails(@Param("id") UUID id);

    List<DamageReportEntity> findByDamageId(UUID damageId);

    List<DamageReportEntity> findByBookingId(UUID bookingId);
}
