package com.dev.rafee.authservice.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.dev.rafee.authservice.dto.FacebookTokenResponse;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FacebookAuthentication {

	@Value("${facebook.app.id}")
	private String appId;

	@Value("${facebook.app.secret}")
	private String appSecret;

	private final RestTemplate restTemplate = new RestTemplate();

	public Map<String, String> validateToken(String token) {

		Map<String, String> userData = new HashMap<>();

		String cred = appId +"|" + appSecret;

		String url = "https://graph.facebook.com/debug_token?input_token=" + token+ "&access_token=" + cred;

		log.info("url : {}",url);

		// ResponseEntity<FacebookTokenResponse> response = restTemplate.getForEntity(url,  FacebookTokenResponse.class);
		ResponseEntity<String> response = restTemplate.getForEntity(url,  String.class);

		log.info("response : {}", response.getBody().toString());

//		if(response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
//			return null;
//		}

		try {
			JSONObject debugJson = new JSONObject(response.getBody());
			boolean isValid = debugJson.getJSONObject("data").getBoolean("is_valid");
			
			log.info("isValid : "+isValid);

			if (isValid) {
				String userId = debugJson.getJSONObject("data").getString("user_id");

				// Fetch user details
				String userUrl = "https://graph.facebook.com/v12.0/" + userId + "?fields=id,name,email&access_token=" + token;
				log.info("User Info URL: {}", userUrl);

				ResponseEntity<String> userResponse = restTemplate.getForEntity(userUrl, String.class);
				log.info("User Response: {}", userResponse.getBody());

				JSONObject userJson = new JSONObject(userResponse.getBody());
				userData.put("id", userJson.optString("id"));
				userData.put("name", userJson.optString("name"));
				userData.put("email", userJson.optString("email")); // Might be empty if not provided by Facebook

			} else {
				userData.put("error", "Invalid token");
			}
		} catch (Exception e) {
			log.error("Error processing token validation: {}", e.getMessage());
			userData.put("error", "Failed to validate token");
		}

		return userData;

	}

}
