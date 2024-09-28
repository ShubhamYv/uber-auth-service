package com.authservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authservice.dtos.PassengerDto;
import com.authservice.dtos.PassengerSignupRequestDto;
import com.authservice.pojo.PassengerSignupRequest;
import com.authservice.services.AuthService;
import com.authservice.utils.PassengerSignupMapper;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;
	private final PassengerSignupMapper passengerSignupMapper;

	public AuthController(AuthService authService, PassengerSignupMapper passengerSignupMapper) {
		this.authService = authService;
		this.passengerSignupMapper = passengerSignupMapper;
	}

	@PostMapping("/signup")
	public ResponseEntity<PassengerDto> signUp(@RequestBody PassengerSignupRequest request) {
		PassengerSignupRequestDto passengerSignupRequestDto = passengerSignupMapper.pojoToDto(request);
		PassengerDto passengerDto = authService.signupPassenger(passengerSignupRequestDto);
		return new ResponseEntity<>(passengerDto, HttpStatus.CREATED);
	}
}
