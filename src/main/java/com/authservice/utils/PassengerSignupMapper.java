package com.authservice.utils;

import org.springframework.stereotype.Component;

import com.authservice.dtos.PassengerSignupRequestDto;
import com.authservice.pojo.PassengerSignupRequest;

@Component
public class PassengerSignupMapper {

    // Convert POJO to DTO
    public PassengerSignupRequestDto pojoToDto(PassengerSignupRequest pojo) {
        return PassengerSignupRequestDto.builder()
                .email(pojo.getEmail())
                .password(pojo.getPassword())
                .phoneNumber(pojo.getPhoneNumber())
                .name(pojo.getName())
                .build();
    }

    // Convert DTO to POJO
    public PassengerSignupRequest dtoToPojo(PassengerSignupRequestDto dto) {
        return PassengerSignupRequest.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .phoneNumber(dto.getPhoneNumber())
                .name(dto.getName())
                .build();
    }
}
