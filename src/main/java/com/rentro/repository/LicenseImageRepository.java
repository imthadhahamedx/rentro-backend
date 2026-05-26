package com.rentro.repository;

import com.rentro.entity.LicenseImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LicenseImageRepository extends JpaRepository<LicenseImageEntity, UUID> {

}
