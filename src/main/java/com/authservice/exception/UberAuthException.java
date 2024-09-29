package com.authservice.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UberAuthException extends RuntimeException {

	private static final long serialVersionUID = -4243771554794901262L;
	private String errorCode;
	private String errorMessage;
	private HttpStatus httpStatus;
}