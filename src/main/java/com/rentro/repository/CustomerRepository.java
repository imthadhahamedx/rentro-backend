package com.rentro.repository;

import com.rentro.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {
    Optional<CustomerEntity> findByNic(String nic);
    Optional<CustomerEntity> findByDrivingLicenseNo(String drivingLicenseNo);
    Optional<CustomerEntity> findByUserId(Integer userId);
}
