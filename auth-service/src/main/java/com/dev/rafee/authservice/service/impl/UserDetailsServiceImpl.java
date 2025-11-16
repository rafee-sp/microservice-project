
package com.dev.rafee.authservice.service.impl;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dev.rafee.authservice.entity.Users;
import com.dev.rafee.authservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Optional<Users> optionalUser = userRepository.findByEmail(email);

		if (!optionalUser.isPresent()) {
		    log.info("user not present");
		    throw new UsernameNotFoundException("User with email : " + email);
		}

		Users user = optionalUser.get();

		return new User(
		        user.getEmail(),
		        user.getPassword(),
		        Collections.emptyList()
		        );
	
	}

}
