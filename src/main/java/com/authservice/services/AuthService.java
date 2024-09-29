package com.authservice.services;

import com.authservice.dtos.PassengerDto;
import com.authservice.dtos.PassengerSigninRequestDto;
import com.authservice.dtos.PassengerSignupRequestDto;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
	public PassengerDto signupPassenger(PassengerSignupRequestDto passengerSignupRequestDto);

	String signInPassenger(PassengerSigninRequestDto passengerSigninRequestDto, HttpServletResponse response);
}