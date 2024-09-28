package com.authservice.services;

import com.authservice.dtos.PassengerDto;
import com.authservice.dtos.PassengerSignupRequestDto;

public interface AuthService {
	public PassengerDto signupPassenger(PassengerSignupRequestDto passengerSignupRequestDto);
}
