package com.rentro.repository;

import com.rentro.entity.DamageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DamageRepository extends JpaRepository<DamageEntity, UUID> {

}
