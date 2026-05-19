package com.rentro.repository;

import com.rentro.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Integer> {

    List<AddressEntity> findByCustomerId(Integer customerId);

}

