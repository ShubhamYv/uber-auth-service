package com.authservice.dtos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDto {
	private Long id;
	private String name;
	private String email;
	private String password;
	private String phoneNumber;
	private Date createdAt;
}
