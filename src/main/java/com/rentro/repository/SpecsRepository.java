package com.rentro.repository;

import com.rentro.entity.SpecsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecsRepository extends JpaRepository<SpecsEntity, Integer> {

    List<SpecsEntity> findByIsActive(Boolean isActive);
}
