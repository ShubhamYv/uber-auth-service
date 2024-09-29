package com.authservice.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.authservice.constants.ErrorCodeEnum;
import com.authservice.dtos.PassengerDto;
import com.authservice.dtos.PassengerSigninRequestDto;
import com.authservice.dtos.PassengerSignupRequestDto;
import com.authservice.exception.UberAuthException;
import com.authservice.pojo.PassengerSigninRequest;
import com.authservice.pojo.PassengerSignupRequest;
import com.authservice.services.AuthService;
import com.authservice.utils.LogMessage;
import com.authservice.utils.PassengerSigninMapper;
import com.authservice.utils.PassengerSignupMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private static final Logger LOGGER = LogManager.getLogger(AuthController.class);

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
		LogMessage.setLogMessagePrefix("/SIGNUP_PASSENGER");

		if (request == null) {
			LogMessage.log(LOGGER, "Received null signup request");
			return ResponseEntity.badRequest().build();
		}

		LogMessage.log(LOGGER, "Processing passenger signup request: " + request);

		PassengerSignupRequestDto passengerSignupRequestDto = passengerSignupMapper.pojoToDto(request);
		PassengerDto passengerDto = authService.signupPassenger(passengerSignupRequestDto);

		LogMessage.log(LOGGER, "Passenger signed up successfully: " + passengerDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(passengerDto);
	}

	@PostMapping("/signin/passenger")
	public ResponseEntity<String> signIn(@RequestBody PassengerSigninRequest request,
			HttpServletResponse httpServletResponse) {
		LogMessage.setLogMessagePrefix("/SIGNIN_PASSENGER");

		if (request == null) {
			LogMessage.log(LOGGER, "Received null signin request");
			return ResponseEntity.badRequest().build();
		}

		LogMessage.log(LOGGER, "Processing passenger signin request: " + request);

		PassengerSigninRequestDto passengerSigninRequestDto = passengerSigninMapper.pojoToDto(request);
		String jwtToken = authService.signInPassenger(passengerSigninRequestDto, httpServletResponse);

		LogMessage.log(LOGGER, "Passenger signed in successfully. JWT Token: " + jwtToken);

		return ResponseEntity.ok(jwtToken);
	}

	@GetMapping("/validate")
	public ResponseEntity<String> validate(HttpServletRequest request) {
		LogMessage.setLogMessagePrefix("/VALIDATE");
		try {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					LogMessage.log(LOGGER, "Cookie:: name->" + cookie.getName() + " |value-> " + cookie.getValue());
				}
			} else {
				LogMessage.log(LOGGER, "No cookies found in the request");
			}
			return ResponseEntity.ok("Success");
		} catch (Exception e) {
			LogMessage.logException(LOGGER, e);
			throw new UberAuthException(ErrorCodeEnum.GENERIC_EXCEPTION.getErrorMessage(),
					ErrorCodeEnum.GENERIC_EXCEPTION.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
