package com.dev.rafee.authservice.dto;

import lombok.Data;

@Data
public class OtpVerificationRequest {

	private Long userId;
	private String otp;
}
