package com.rentro.repository;

import com.rentro.entity.DamageReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DamageReportRepository extends JpaRepository<DamageReportEntity, UUID> {

}
