package com.authservice.utils;

import org.springframework.stereotype.Component;

import com.authservice.dtos.PassengerSigninRequestDto;
import com.authservice.pojo.PassengerSigninRequest;

@Component
public class PassengerSigninMapper {

	public PassengerSigninRequest dtoToPojo(PassengerSigninRequestDto passengerSigninRequestDto) {
		if (passengerSigninRequestDto == null) {
			return null;
		}

		PassengerSigninRequest pojo = new PassengerSigninRequest();
		pojo.setEmail(passengerSigninRequestDto.getEmail());
		pojo.setPassword(passengerSigninRequestDto.getPassword());

		return pojo;
	}

	public PassengerSigninRequestDto pojoToDto(PassengerSigninRequest pojo) {
		if (pojo == null) {
			return null;
		}

		return new PassengerSigninRequestDto(pojo.getEmail(), pojo.getPassword());
	}
}
