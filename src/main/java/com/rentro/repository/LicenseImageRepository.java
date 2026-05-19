package com.rentro.repository;

import com.rentro.entity.LicenseImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicenseImageRepository extends JpaRepository<LicenseImageEntity, Integer> {

    List<LicenseImageEntity> findByCustomerId(Integer customerId);
}
