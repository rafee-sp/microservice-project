package com.dev.rafee.authservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.rafee.authservice.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

	boolean existsByEmail(String email);

	Optional<Users> findByEmail(String email);
}
