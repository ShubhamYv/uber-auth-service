package com.authservice.services;

import org.springframework.security.core.Authentication;

public interface JwtProvider {
	String generateToken(Authentication auth);

	String getEmailFromJwtToken(String jwt);
}
