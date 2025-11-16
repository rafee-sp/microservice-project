package com.dev.rafee.authservice.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.rafee.authservice.dto.ApiResponse;
import com.dev.rafee.authservice.dto.AuthRequest;
import com.dev.rafee.authservice.dto.FacebookRequest;
import com.dev.rafee.authservice.dto.GoogleRequest;
import com.dev.rafee.authservice.dto.OtpVerificationRequest;
import com.dev.rafee.authservice.dto.ResendOTPRequest;
import com.dev.rafee.authservice.entity.Users;
import com.dev.rafee.authservice.repository.UserRepository;
import com.dev.rafee.authservice.service.EmailService;
import com.dev.rafee.authservice.service.UserService;
import com.dev.rafee.authservice.util.FacebookAuthentication;
import com.dev.rafee.authservice.util.GoogleAuthentication;
import com.dev.rafee.authservice.util.JwtUtil;
import com.google.auth.oauth2.TokenVerifier.VerificationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	private final JwtUtil jwtUtil;
	private final UserRepository userRepo;
	private final EmailService emailService;
	private final GoogleAuthentication googleAuth;
	private final FacebookAuthentication facebookAuth;


	@PostMapping("/register")
	public ResponseEntity<ApiResponse<Map<String, Long>>> handleRegister(@RequestBody AuthRequest authRequest,
			HttpServletResponse response) {

		log.info("user email : {} ", authRequest.getEmail());

		try {

			// check existing user
			if (userRepo.existsByEmail(authRequest.getEmail())) {

				log.info("User aleady exists");

				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(new ApiResponse<Map<String, Long>>(409, "User aleady exists", null));

			}

			// create user and user profile
			Users createdUser = userService.createUserWithProfile(authRequest.getEmail(), authRequest.getPassword(),
					authRequest.getUserName(), false, "local");

			// send otp
			emailService.sendVerificationCode(createdUser, "new");

			// send response
			Map<String, Long> userMap = new HashMap<String, Long>();

			userMap.put("userId", createdUser.getId());

			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new ApiResponse<Map<String, Long>>(201, "User created and otp sent successfully", userMap));

		} catch (Exception e) {

			log.error("An exception occurred at handleRegister", e);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<Map<String, Long>>(500, "Internal server error", null));

		}
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestBody OtpVerificationRequest request,
			HttpServletRequest httpRequest, HttpServletResponse response) {

		log.info("user email : {} ", request.getUserId());

		try {

			boolean isVerified = userService.verifyOtp(request.getUserId(), request.getOtp());

			if (isVerified) {

				Users user = userRepo.findById(request.getUserId()).get();

				response.addCookie(jwtUtil.createCookie(user));

				return ResponseEntity.status(HttpStatus.OK)
						.body(new ApiResponse<String>(200, "Authentication success", null));

			} else {

				log.warn("Incorrect otp");

				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(new ApiResponse<String>(401, "Invalid OTP", null));

			}

		} catch (Exception e) {

			log.error("An exception occurred at verifyOtp", e);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<String>(500, "Internal server error", null));

		}
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<String>> handleLogin(@RequestBody AuthRequest authRequest,
			HttpServletResponse response) {

		try {

			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));	// TODO: common service

			log.info("User exists and password matched");

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();

			response.addCookie(jwtUtil.createCookie(userDetails));

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<String>(200, "Sign-in success", null));

		} catch (BadCredentialsException e) {

			log.info("An exception occurred at handleLogin", e);

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ApiResponse<String>(401, "Invalid credentials", null));

		} catch (Exception e) {
			
		    log.info("An exception occurred at handleLogin", e);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<String>(500, "Internal server error", null));

		}

	}

	@PostMapping("/validate-google-token")
	public ResponseEntity<ApiResponse<String>> googleValidate(@RequestBody GoogleRequest request,
			HttpServletResponse response) {

		try {

			log.info("token : " + request.getAuthToken());

			Map<String, String> userDetailsMap = googleAuth.validateToken(request.getAuthToken());

			if(userDetailsMap == null || userDetailsMap.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(new ApiResponse<String>(401, "Invalid google auth token", null));
			}


			String email = userDetailsMap.get("email");
			String userName = userDetailsMap.get("name");

			Optional<Users> existingUser = userRepo.findByEmail(email);

			if (existingUser.isPresent()) {

				response.addCookie(jwtUtil.createCookie(existingUser.get()));

				return ResponseEntity.status(HttpStatus.OK)
						.body(new ApiResponse<String>(200, "Sign-in success", null));

			} else {

				Users user = userService.createUserWithProfile(email, null, userName, true, "google"); // password null
				// for google
				// auth

				response.addCookie(jwtUtil.createCookie(user));

				return ResponseEntity.status(HttpStatus.OK)
						.body(new ApiResponse<String>(200, "Sign-in success", null));

			}


		} catch (VerificationException e) {

			log.error("An exception occurred at googleValidate", e);

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ApiResponse<String>(401, "Invalid google auth token", null));

		} catch (Exception e) {

			log.error("An exception occurred at googleValidate", e);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<String>(500, "Internal server error", null));
		}
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<ApiResponse<String>> resendOtp(@RequestBody ResendOTPRequest request) {

		log.info("user email : {} ", request.getUserId());

		try {

			Users user = userRepo.findById(request.getUserId())
					.orElseThrow(() -> new RuntimeException("No users found"));

			// send otp
			emailService.sendVerificationCode(user, "resend");

			log.info("sending response");

			return ResponseEntity.status(HttpStatus.OK)
					.body(new ApiResponse<String>(200, "OTP sent successfully", null));

		} catch (Exception e) {

			log.error("An exception occurred at resendOtp", e);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<String>(500, "Internal server error", null));

		}
	}

	@PostMapping("/validate-facebook-token")
	public ResponseEntity<ApiResponse<String>> facebookValidate(@RequestBody FacebookRequest request,
			HttpServletResponse response) {

		try {

			log.info("token : " + request.getAuthToken());

			Map<String, String> userDetailsMap  = facebookAuth.validateToken(request.getAuthToken());


			if(userDetailsMap == null || userDetailsMap.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(new ApiResponse<String>(401, "Invalid facebook auth token", null));
			}


			String email = userDetailsMap.get("email");
			String userName = userDetailsMap.get("name");

			log.info("email : {}",email);
			log.info("name : {}",userName);

			Optional<Users> existingUser = userRepo.findByEmail(email);

			if (existingUser.isPresent()) {

				response.addCookie(jwtUtil.createCookie(existingUser.get()));

				return ResponseEntity.status(HttpStatus.OK)
						.body(new ApiResponse<String>(200, "Sign-in success", null));

			} else {

				Users user = userService.createUserWithProfile(email, null, userName, true, "facebook"); // TODO : Create enums

				response.addCookie(jwtUtil.createCookie(user));

				return ResponseEntity.status(HttpStatus.OK)
						.body(new ApiResponse<String>(200, "Sign-in success", null));

			}


		} catch (Exception e) {

			log.error("An exception occurred at facebookValidate", e);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<String>(500, "Internal server error", null));
		}
	}

}