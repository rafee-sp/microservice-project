package com.dev.rafee.authservice.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="users")
public class Users {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "email", nullable = false, length = 100)
	private String email;
	
	@Column(name = "password", length = 100)
	private String password;
	
	@Column(name = "user_name", nullable = false, length = 45)
	private String userName;
	
	@Column(name = "provider", nullable = false, length = 45)
	private String provider;  // TODO : ENUMS
	
	@Column(name = "verified")
	private boolean isVerified = false;
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private UserVerification userVerifications;
	
	@CreationTimestamp
	@Column(name="created_at", nullable = false, updatable = false, columnDefinition = "DATETIME")	
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(name="updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;
	
}
