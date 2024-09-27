package com.authservice.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authservice.pojo.PassengerSignupRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@RequestHeader PassengerSignupRequest request) {

		return null;
	}
}
