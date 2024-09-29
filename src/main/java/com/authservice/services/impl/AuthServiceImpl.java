package com.authservice.services.impl;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.authservice.constants.ErrorCodeEnum;
import com.authservice.dtos.PassengerDto;
import com.authservice.dtos.PassengerSigninRequestDto;
import com.authservice.dtos.PassengerSignupRequestDto;
import com.authservice.exception.UberAuthException;
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
            throw new UberAuthException(
                ErrorCodeEnum.JWT_SECRET_NOT_CONFIGURED.getErrorMessage(),
                ErrorCodeEnum.JWT_SECRET_NOT_CONFIGURED.getErrorCode(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public PassengerDto signupPassenger(PassengerSignupRequestDto passengerSignupRequestDto) {
        Optional<Passenger> existingPassenger = passengerRepository.findPassengerByEmail(passengerSignupRequestDto.getEmail());
        
        if (existingPassenger.isPresent()) {
            throw new UberAuthException(
                ErrorCodeEnum.EMAIL_ALREADY_EXISTS.getErrorMessage(),
                ErrorCodeEnum.EMAIL_ALREADY_EXISTS.getErrorCode(),
                HttpStatus.CONFLICT
            );
        }

        try {
            Passenger passenger = Passenger.builder()
                .email(passengerSignupRequestDto.getEmail())
                .name(passengerSignupRequestDto.getName())
                .password(bCryptPasswordEncoder.encode(passengerSignupRequestDto.getPassword()))
                .phoneNumber(passengerSignupRequestDto.getPhoneNumber())
                .build();

            Passenger newPassenger = passengerRepository.save(passenger);
            return passengerMapper.modelToDto(newPassenger);
        } catch (Exception e) {
            throw new UberAuthException(
                ErrorCodeEnum.SIGNUP_FAILED.getErrorMessage(),
                ErrorCodeEnum.SIGNUP_FAILED.getErrorCode(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public String signInPassenger(PassengerSigninRequestDto passengerSigninRequestDto, HttpServletResponse response) {
        try {
        	System.out.println("Inside AuthServiceImpl|signInPassenger||passengerSigninRequestDto::"+passengerSigninRequestDto);
        	System.out.println("Inside AuthServiceImpl|signInPassenger||HttpServletResponse::"+response);
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(passengerSigninRequestDto.getEmail(), 
                                                        passengerSigninRequestDto.getPassword())
            );
            System.out.println("Inside AuthServiceImpl|signInPassenger||authentication::"+authentication);
            String jwtToken = generateToken(authentication);
            System.out.println("Inside AuthServiceImpl|signInPassenger||jwtToken::"+jwtToken);
            response.addCookie(createJwtCookie(jwtToken));
            return jwtToken;
        } catch (AuthenticationException e) {
        	System.out.println("Inside AuthServiceImpl|signInPassenger||AuthenticationException::"+ e);
            throw new UberAuthException(
                ErrorCodeEnum.AUTHENTICATION_FAILED.getErrorMessage(),
                ErrorCodeEnum.AUTHENTICATION_FAILED.getErrorCode(),
                HttpStatus.UNAUTHORIZED
            );
        } catch (Exception e) {
            throw new UberAuthException(
                ErrorCodeEnum.JWT_GENERATION_FAILED.getErrorMessage(),
                ErrorCodeEnum.JWT_GENERATION_FAILED.getErrorCode(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
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
            throw new UberAuthException(
                ErrorCodeEnum.JWT_COOKIE_NAME_NOT_CONFIGURED.getErrorMessage(),
                ErrorCodeEnum.JWT_COOKIE_NAME_NOT_CONFIGURED.getErrorCode(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        Cookie cookie = new Cookie(jwtCookieName, jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(cookieMaxAge);
        return cookie;
    }
}
