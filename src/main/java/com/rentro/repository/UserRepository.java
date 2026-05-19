package com.rentro.repository;

import com.rentro.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    java.util.List<UserEntity> findByRole(UserEntity.Role role);
    java.util.List<UserEntity> findByIsActive(boolean isActive);
    Optional<UserEntity> findUserEntityByEmail(String email);
}
