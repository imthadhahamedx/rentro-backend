package com.rentro.repository;

import com.rentro.entity.SpecsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpecsRepository extends JpaRepository<SpecsEntity, UUID> {

}
