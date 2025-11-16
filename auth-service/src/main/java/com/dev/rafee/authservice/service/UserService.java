package com.dev.rafee.authservice.service;

import com.dev.rafee.authservice.entity.Users;

public interface UserService {

	public Users createUserWithProfile(String email, String password, String userName, boolean isVerified, String provider);

	public boolean verifyOtp(Long userId, String string) throws Exception;
	
}
