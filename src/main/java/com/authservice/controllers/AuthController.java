package com.authservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authservice.constants.ErrorCodeEnum;
import com.authservice.dtos.PassengerDto;
import com.authservice.dtos.PassengerSigninRequestDto;
import com.authservice.dtos.PassengerSignupRequestDto;
import com.authservice.exception.UberAuthException;
import com.authservice.pojo.PassengerSigninRequest;
import com.authservice.pojo.PassengerSignupRequest;
import com.authservice.services.AuthService;
import com.authservice.utils.PassengerSigninMapper;
import com.authservice.utils.PassengerSignupMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;
	private final PassengerSignupMapper passengerSignupMapper;
	private final PassengerSigninMapper passengerSigninMapper;

	public AuthController(AuthService authService, PassengerSignupMapper passengerSignupMapper,
			PassengerSigninMapper passengerSigninMapper) {
		this.authService = authService;
		this.passengerSignupMapper = passengerSignupMapper;
		this.passengerSigninMapper = passengerSigninMapper;
	}

	@PostMapping("/signup/passenger")
	public ResponseEntity<PassengerDto> signUp(@RequestBody PassengerSignupRequest request) {
		PassengerSignupRequestDto passengerSignupRequestDto = passengerSignupMapper.pojoToDto(request);
		PassengerDto passengerDto = authService.signupPassenger(passengerSignupRequestDto);
		return new ResponseEntity<>(passengerDto, HttpStatus.CREATED);
	}

	@PostMapping("/signin/passenger")
	public ResponseEntity<String> signIn(@RequestBody PassengerSigninRequest request,
			HttpServletResponse httpServletResponse) {

		System.out.println("Inside AuthController|signIn||request:" + request);
		PassengerSigninRequestDto passengerSigninRequestDto = passengerSigninMapper.pojoToDto(request);
		System.out.println("Inside AuthController|signIn||passengerSigninRequestDto::" + passengerSigninRequestDto);
		String jwtToken = authService.signInPassenger(passengerSigninRequestDto, httpServletResponse);
		System.out.println("Inside AuthController|signIn||jwtToken::" + jwtToken);
		return new ResponseEntity<>(jwtToken, HttpStatus.OK);
	}

	@GetMapping("/validate")
	public ResponseEntity<String> validate(HttpServletRequest request, HttpServletResponse response) {
		try {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					System.out.println("Cookie: {} = {}" + cookie.getName() + " " + cookie.getValue());
				}
			} else {
				System.out.println("No cookies found in the request");
			}
			return new ResponseEntity<>("Success", HttpStatus.OK);
		} catch (Exception e) {
			System.out.println("Error during validation: {}" + e.getMessage());
			throw new UberAuthException(ErrorCodeEnum.GENERIC_EXCEPTION.getErrorMessage(),
					ErrorCodeEnum.GENERIC_EXCEPTION.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
