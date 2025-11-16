package com.dev.rafee.authservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileDetails {

	private Long id;
	private String userName;
	
	public UserProfileDetails(Long id, String role, String userName) {
        this.id = id;
        this.userName = userName;
    }

}
