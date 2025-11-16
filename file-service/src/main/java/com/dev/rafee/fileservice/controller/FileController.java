package com.dev.rafee.fileservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/file")
public class FileController {

	@GetMapping("/test")
	public String test() {
		return "test";
	}
	
	@PostMapping("/new")
	public ResponseEntity<String> saveFile(){
		return null;
	}
}
