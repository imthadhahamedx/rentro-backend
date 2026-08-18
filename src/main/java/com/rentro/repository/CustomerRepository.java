package com.rentro.repository;

import com.rentro.entity.CustomerEntity;
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
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {

    // Customer has no own createdAt column — it rides on the linked User's createdAt
    @Query("SELECT COUNT(c) FROM CustomerEntity c WHERE c.user.createdAt BETWEEN :start AND :end")
    long countByUserCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT c FROM CustomerEntity c JOIN c.user u " +
            "WHERE :searchText = '' " +
            "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(c.nic) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "ORDER BY u.fullName ASC")
    List<CustomerEntity> search(@Param("searchText") String searchText);


    // ─── Customer management (list / detail) ──────────────────────────────────
    boolean existsByNicIgnoreCase(String nic);

    boolean existsByNicIgnoreCaseAndIdNot(String nic, UUID id);

    @Query("SELECT c FROM CustomerEntity c JOIN c.user u " +
            "WHERE (:searchText = '' " +
            "     OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(c.nic) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "     OR LOWER(c.drivingLicenseNo) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
            "AND (:isActive IS NULL OR u.isActive = :isActive) " +
            "ORDER BY u.fullName ASC")
    Page<CustomerEntity> search(
            @Param("searchText") String searchText,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    @Query("SELECT c FROM CustomerEntity c LEFT JOIN FETCH c.user LEFT JOIN FETCH c.addresses WHERE c.id = :id")
    Optional<CustomerEntity> findByIdWithDetails(@Param("id") UUID id);
}
