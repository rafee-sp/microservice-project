package com.dev.rafee.authservice.service;

import com.dev.rafee.authservice.entity.Users;

public interface EmailService {
	
	public void sendVerificationCode(Users user, String type);

}
