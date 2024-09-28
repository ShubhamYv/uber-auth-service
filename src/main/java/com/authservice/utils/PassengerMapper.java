package com.authservice.utils;

import org.springframework.stereotype.Component;

import com.authservice.dtos.PassengerDto;
import com.authservice.models.Passenger;

@Component
public class PassengerMapper {

    // Convert Passenger Model to PassengerDto
    public PassengerDto modelToDto(Passenger passenger) {
        if (passenger == null) {
            return null;
        }
        return PassengerDto.builder()
                .id(passenger.getId())
                .name(passenger.getName())
                .email(passenger.getEmail())
                .password(passenger.getPassword())
                .phoneNumber(passenger.getPhoneNumber())
                .createdAt(passenger.getCreatedAt())
                .build();
    }

    // Convert PassengerDto to Passenger Model
    public Passenger dtoToModel(PassengerDto passengerDto) {
        if (passengerDto == null) {
            return null;
        }
        
        Passenger passenger = Passenger.builder()
                .name(passengerDto.getName())
                .email(passengerDto.getEmail())
                .password(passengerDto.getPassword())
                .phoneNumber(passengerDto.getPhoneNumber())
                .build();
                
        passenger.setId(passengerDto.getId());
        
        return passenger;
    }
}
