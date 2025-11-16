package com.dev.rafee.authservice.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="user_verification")
public class UserVerification {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
		
	@Column(name="verification_code")
	private String verificationCode;
	
		
	@Column(name="expiry_at")
	private LocalDateTime expiryAt;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private Users user;
	
	@Column(name="created_at", nullable = false, updatable = false, columnDefinition = "DATETIME")
	@CreationTimestamp
	private LocalDateTime createdAt;
}
