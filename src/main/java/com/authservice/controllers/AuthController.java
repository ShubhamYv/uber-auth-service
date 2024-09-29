package com.authservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authservice.dtos.PassengerDto;
import com.authservice.dtos.PassengerSigninRequestDto;
import com.authservice.dtos.PassengerSignupRequestDto;
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
        try {
            PassengerSignupRequestDto passengerSignupRequestDto = passengerSignupMapper.pojoToDto(request);
            PassengerDto passengerDto = authService.signupPassenger(passengerSignupRequestDto);
            return new ResponseEntity<>(passengerDto, HttpStatus.CREATED);
        } catch (Exception e) {
        	System.out.println("Error while signup!");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/signin/passenger")
    public ResponseEntity<String> signIn(@RequestBody PassengerSigninRequest request,
                                          HttpServletResponse httpServletResponse) {
        try {
            PassengerSigninRequestDto passengerSigninRequestDto = passengerSigninMapper.pojoToDto(request);
            String jwtToken = authService.signInPassenger(passengerSigninRequestDto, httpServletResponse);
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception and return an appropriate error response
            return new ResponseEntity<>("Sign-in failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
