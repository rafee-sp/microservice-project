package com.dev.rafee.authservice.service.impl;


import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.dev.rafee.authservice.entity.UserVerification;
import com.dev.rafee.authservice.entity.Users;
import com.dev.rafee.authservice.repository.UserRepository;
import com.dev.rafee.authservice.repository.UserVerificationRepository;
import com.dev.rafee.authservice.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
		
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserVerificationRepository userVerificationRepository;
	
	@Override
	public Users createUserWithProfile(String email, String password, String userName, boolean isVerified, String provider)  {

		// save user credentials
		Users user = new Users();
		user.setEmail(email);
		if(provider.equalsIgnoreCase("google") || provider.equalsIgnoreCase("facebook")) {
			user.setPassword(null);
		} else {
			user.setPassword(passwordEncoder.encode(password));
		}
		user.setProvider(provider);
		user.setUserName(userName);
		user.setVerified(isVerified);
		Users savedUser = userRepository.save(user);
			
		log.info("user created successfully");
		log.info("user Id : "+savedUser.getId());
				
		return savedUser;

	}
	

	public boolean verifyOtp(Long userId, String otp) throws Exception {

		Optional<UserVerification> userVerification = userVerificationRepository.findByUserId(userId);

		if(userVerification.isPresent() && userVerification.get().getVerificationCode().equals(otp) && 
				userVerification.get().getExpiryAt().isAfter(LocalDateTime.now())) {

			log.info("Records exisits and OTP matched");

			Users user  = userRepository.findById(userId).get();
			user.setVerified(true);
			userRepository.save(user);

			userVerificationRepository.deleteByIdNative(userVerification.get().getId());

			return true;
		}

		return false;
	}

}