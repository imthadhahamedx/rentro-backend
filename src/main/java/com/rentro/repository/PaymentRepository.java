package com.rentro.repository;

import com.rentro.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer> {

    Optional<PaymentEntity> findByPaymentRef(String paymentRef);
    List<PaymentEntity> findByBookingId(Integer bookingId);
    List<PaymentEntity> findByStatus(PaymentEntity.PaymentStatus status);
    List<PaymentEntity> findByBookingIdAndPaymentType(Integer bookingId, PaymentEntity.PaymentType paymentType);
}
