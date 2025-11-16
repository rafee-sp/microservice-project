package com.dev.rafee.authservice.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookTokenResponse {
	
	private FacebookData data;
	
		@Data
	    @JsonIgnoreProperties(ignoreUnknown = true)
	    public static class FacebookData {
	        private String app_id;
	        private String type;
	        private String application;
	        private long data_access_expires_at;
	        private long expires_at;
	        private boolean is_valid;
	        private List<String> scopes;
	        private String user_id;
	        
	        
	        public void setIs_valid(boolean valid) {
	        	this.is_valid = valid;
	        }
	    }

}
