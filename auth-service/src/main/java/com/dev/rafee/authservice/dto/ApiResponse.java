package com.dev.rafee.authservice.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
	
	private int statusCode;
	private String message;
	private T result;
	
	public ApiResponse(int statusCode, String message, T result) {
		
		this.statusCode = statusCode;
		this.message = message;
		this.result = result;
	}

}