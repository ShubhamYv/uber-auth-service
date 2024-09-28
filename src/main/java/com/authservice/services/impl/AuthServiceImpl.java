package com.authservice.services.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.authservice.dtos.PassengerDto;
import com.authservice.dtos.PassengerSignupRequestDto;
import com.authservice.models.Passenger;
import com.authservice.repositories.PassengerRepository;
import com.authservice.services.AuthService;
import com.authservice.utils.PassengerMapper;

@Service
public class AuthServiceImpl implements AuthService {

	private final PassengerRepository passengerRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final PassengerMapper passengerMapper;

	public AuthServiceImpl(PassengerRepository passengerRepository, BCryptPasswordEncoder bCryptPasswordEncoder ,PassengerMapper passengerMapper) {
		this.passengerRepository = passengerRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.passengerMapper = passengerMapper;
	}

	@Override
	public PassengerDto signupPassenger(PassengerSignupRequestDto passengerSignupRequestDto) {
        Passenger passenger = Passenger.builder()
                .email(passengerSignupRequestDto.getEmail())
                .name(passengerSignupRequestDto.getName())
                .password(bCryptPasswordEncoder.encode(passengerSignupRequestDto.getPassword())) // TODO: Encrypt the password
                .phoneNumber(passengerSignupRequestDto.getPhoneNumber())
                .build();
        
        Passenger newPassenger = passengerRepository.save(passenger);
		return passengerMapper.modelToDto(newPassenger);
	}

}
