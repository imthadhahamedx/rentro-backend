package com.rentro.repository;

import com.rentro.entity.DamageReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DamageReportRepository extends JpaRepository<DamageReportEntity, Integer> {

    List<DamageReportEntity> findByBookingId(Integer bookingId);
    List<DamageReportEntity> findByDamageId(Integer damageId);
    List<DamageReportEntity> findByReviewedById(Integer userId);
}
