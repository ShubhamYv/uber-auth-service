package com.authservice.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerSigninResponse {
	private Boolean success;
}