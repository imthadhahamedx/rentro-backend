package com.rentro.repository;

import com.rentro.entity.BookingExtensionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingExtensionRepository extends JpaRepository<BookingExtensionEntity, UUID> {

}
