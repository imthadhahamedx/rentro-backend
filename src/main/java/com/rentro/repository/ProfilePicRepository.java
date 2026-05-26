package com.rentro.repository;

import com.rentro.entity.ProfilePicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfilePicRepository extends JpaRepository<ProfilePicEntity, UUID> {

}
