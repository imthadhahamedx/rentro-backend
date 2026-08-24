package com.rentro.repository;

import com.rentro.entity.BookingEntity;
import com.rentro.entity.PaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

    // ─── Reports: completed payments within a period, fully hydrated ──────────
    @Query("SELECT p FROM PaymentEntity p " +
            "JOIN FETCH p.booking b JOIN FETCH b.vehicle v " +
            "WHERE p.status = :status AND p.paidAt BETWEEN :start AND :end " +
            "ORDER BY p.paidAt DESC")
    List<PaymentEntity> findCompletedForReport(
            @Param("status") PaymentEntity.PaymentStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT p FROM PaymentEntity p " +
            "WHERE (:status IS NULL OR p.status = :status) " +
            "AND (" +
            ":searchText IS NULL " +
            "OR :searchText = '' " +
            "OR LOWER(p.paymentRef) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(p.transactionId) LIKE LOWER(CONCAT('%', :searchText, '%'))" +
            ")")
    Page<PaymentEntity> findAllByStatus(
            @Param("searchText") String searchText,
            @Param("status") PaymentEntity.PaymentStatus status,
            Pageable pageable
    );
}
