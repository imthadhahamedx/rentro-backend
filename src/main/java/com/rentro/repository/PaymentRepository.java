package com.rentro.repository;

import com.rentro.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {

    long countByStatus(PaymentEntity.PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentEntity p WHERE p.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") PaymentEntity.PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentEntity p " +
            "WHERE p.status = :status AND p.paidAt BETWEEN :start AND :end")
    BigDecimal sumAmountByStatusAndPaidAtBetween(
            @Param("status") PaymentEntity.PaymentStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
