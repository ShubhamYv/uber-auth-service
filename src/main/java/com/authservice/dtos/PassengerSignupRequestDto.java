package com.authservice.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerSignupRequestDto {
	private String email;
	private String password;
	private String phoneNumber;
	private String name;
}
