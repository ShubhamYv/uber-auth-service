package com.authservice.constants;

import lombok.Getter;

public enum ErrorCodeEnum {

	GENERIC_EXCEPTION("10000", "Something went wrong, please try later."),
	JWT_SECRET_NOT_CONFIGURED("10001", "JWT secret key is not configured."),
	SIGNUP_FAILED("10002", "Failed to sign up the passenger."),
	AUTHENTICATION_FAILED("10003", "Invalid email or password."),
	JWT_GENERATION_FAILED("10004", "Failed to generate JWT token."),
	JWT_COOKIE_NAME_NOT_CONFIGURED("10005", "JWT cookie name is not configured."),
	EMAIL_ALREADY_EXISTS("10006", "The email is already in use."),
	NULL_REQUEST_DTO("10007", "Passenger signup request DTO cannot be null.");

	@Getter
	private final String errorCode;
	@Getter
	private final String errorMessage;

	private ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}
