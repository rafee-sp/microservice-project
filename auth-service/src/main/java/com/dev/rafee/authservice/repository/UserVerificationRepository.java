package com.dev.rafee.authservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dev.rafee.authservice.entity.UserVerification;

import jakarta.transaction.Transactional;

public interface UserVerificationRepository extends JpaRepository<UserVerification, Long>{

	Optional<UserVerification> findByUserId(Long userId);
	
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_verification WHERE id = :id", nativeQuery = true)
    void deleteByIdNative(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_verification WHERE user_id = :id", nativeQuery = true)
    void deleteByUserId(@Param("id") Long id);


}
