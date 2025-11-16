package com.dev.rafee.authservice.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.json.webtoken.JsonWebToken.Payload;
import com.google.auth.oauth2.TokenVerifier;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GoogleAuthentication {
	
	@Value("${goole.clientId}")
	private String googleClientId; 
	
	public Map<String, String> validateToken(String token) throws Exception {

			TokenVerifier verifier = TokenVerifier.newBuilder().setAudience(googleClientId).build();

			JsonWebSignature jsonWebSignature = verifier.verify(token);

			Payload payload = jsonWebSignature.getPayload();

			String email = payload.get("email").toString();
			String name = payload.get("name").toString();

			Map<String, String> resultMap = new HashMap<>();

			resultMap.put("email", email);
			resultMap.put("name", name);

			return resultMap;

	}
	
}
