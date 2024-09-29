package com.authservice.services.impl;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.authservice.dtos.PassengerDto;
import com.authservice.dtos.PassengerSigninRequestDto;
import com.authservice.dtos.PassengerSignupRequestDto;
import com.authservice.models.Passenger;
import com.authservice.repositories.PassengerRepository;
import com.authservice.services.AuthService;
import com.authservice.utils.PassengerMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiry}")
    private int jwtExpiry;

    @Value("${jwt.cookie.name}")
    private String jwtCookieName;

    @Value("${jwt.cookie.maxAge}")
    private int cookieMaxAge;

    private SecretKey key;

    private final PassengerRepository passengerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PassengerMapper passengerMapper;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(PassengerRepository passengerRepository, 
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           PassengerMapper passengerMapper, 
                           AuthenticationManager authenticationManager) {
        this.passengerRepository = passengerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.passengerMapper = passengerMapper;
        this.authenticationManager = authenticationManager;
    }

    @PostConstruct
    private void init() {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("JWT secret key must be configured");
        }
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public PassengerDto signupPassenger(PassengerSignupRequestDto passengerSignupRequestDto) {
        Passenger passenger = Passenger.builder()
                .email(passengerSignupRequestDto.getEmail())
                .name(passengerSignupRequestDto.getName())
                .password(bCryptPasswordEncoder.encode(passengerSignupRequestDto.getPassword()))
                .phoneNumber(passengerSignupRequestDto.getPhoneNumber())
                .build();

        Passenger newPassenger = passengerRepository.save(passenger);
        return passengerMapper.modelToDto(newPassenger);
    }

    public String signInPassenger(PassengerSigninRequestDto passengerSigninRequestDto, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(passengerSigninRequestDto.getEmail(), 
                                                         passengerSigninRequestDto.getPassword())
            );
            String jwtToken = generateToken(authentication);
            response.addCookie(createJwtCookie(jwtToken));

            return jwtToken;
        } catch (AuthenticationException e) {
            System.err.println("Authentication failed: " + e.getMessage());
            throw new RuntimeException("Invalid username or password", e);
        }
    }

    private String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiry * 1000L);
        
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("email", authentication.getName())
                .signWith(key)
                .compact();
    }

    private Cookie createJwtCookie(String jwt) {
        if (jwtCookieName == null || jwtCookieName.isEmpty()) {
            throw new IllegalArgumentException("JWT cookie name must be configured");
        }
        
        Cookie cookie = new Cookie(jwtCookieName, jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(cookieMaxAge);
        return cookie;
    }
}
