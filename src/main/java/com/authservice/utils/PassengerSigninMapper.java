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

		return PassengerSigninRequest
				.builder()
				.email(passengerSigninRequestDto.getEmail())
				.password(passengerSigninRequestDto.getPassword())
				.build();
	}

	public PassengerSigninRequestDto pojoToDto(PassengerSigninRequest pojo) {
		if (pojo == null) {
			return null;
		}

		return PassengerSigninRequestDto
				.builder()
				.email(pojo.getEmail())
				.password(pojo.getPassword())
				.build();
	}
}
