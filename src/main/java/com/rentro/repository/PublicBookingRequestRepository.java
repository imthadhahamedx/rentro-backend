package com.rentro.repository;

import com.rentro.entity.PublicBookingRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PublicBookingRequestRepository extends JpaRepository<PublicBookingRequestEntity, UUID> {

    boolean existsByBookingRef(String bookingRef);
}
