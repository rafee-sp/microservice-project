package com.dev.rafee.authservice.util;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.dev.rafee.authservice.entity.Users;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {
	
	private final SecretKey secretKey;
	
	@Value("${jwt.expiration.refresh}")
	private Long jwtExpirationTime;
	
	public JwtUtil(@Value("${jwt.secret}") String base64Secret) {

		this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
	}
	
	public String generate(Object principal) {
		
		String subject = null;
		
		if(principal instanceof UserDetails) {
			
			UserDetails userDetails = (UserDetails) principal;

			subject = userDetails.getUsername();
			
		} else if (principal instanceof Users) {
			
			Users user = (Users) principal;
			subject = user.getEmail();
		}
		
		return Jwts.builder()
				.claim("sub", subject)				   
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
				.signWith(secretKey, Jwts.SIG.HS256)
				.compact();
	}
	
		
	public String extractEmail(String token) {
		
		return getClaims(token).getSubject();
	}
	
	private Claims getClaims(String token) {
		
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
				
	} 
	
	/*
	 * public List<SimpleGrantedAuthority> extractAuthorities(String token){
	 * 
	 * String roles = getClaims(token).get("roles", String.class);
	 * 
	 * return Arrays.stream(roles.split(",")) .map(SimpleGrantedAuthority::new)
	 * .collect(Collectors.toList());
	 * 
	 * }
	 */
	
	public boolean isTokenValid(String token) {
		
			return !getClaims(token).getExpiration().before(new Date());
		
	}
	

	public Cookie createCookie(Object principal) {
		
		String token = generate(principal);

		Cookie jwtCookie = new Cookie("refresh-token", token);
		jwtCookie.setHttpOnly(true);
		jwtCookie.setPath("/"); // set to refresh path
		jwtCookie.setMaxAge((int) (jwtExpirationTime / 1000));
		jwtCookie.setSecure(false);
		
		return jwtCookie;

	}
	
	public String extractJwtFromRequest(HttpServletRequest request) {

		return java.util.Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equalsIgnoreCase("refresh-token"))
				.map(Cookie::getValue)
				.findFirst()
				.orElse(null);
	}


}
