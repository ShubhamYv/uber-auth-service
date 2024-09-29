package com.authservice.dtos;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerDto {
	private Long id;
	private String name;
	private String email;
	private String password;
	private String phoneNumber;
	private Date createdAt;
}
