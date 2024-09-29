package com.authservice.services.impl;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.authservice.utils.LogMessage;
import com.authservice.utils.PassengerMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);

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
        if (passengerSignupRequestDto == null) {
            throw new UberAuthException(
                ErrorCodeEnum.NULL_REQUEST_DTO.getErrorMessage(),
                ErrorCodeEnum.NULL_REQUEST_DTO.getErrorCode(),
                HttpStatus.BAD_REQUEST
            );
        }

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
            LogMessage.log(logger, "New passenger signed up: "+ newPassenger.getEmail());
            return passengerMapper.modelToDto(newPassenger);
        } catch (Exception e) {
            LogMessage.logException(logger, e);
            throw new UberAuthException(
                ErrorCodeEnum.SIGNUP_FAILED.getErrorMessage(),
                ErrorCodeEnum.SIGNUP_FAILED.getErrorCode(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public String signInPassenger(PassengerSigninRequestDto passengerSigninRequestDto, HttpServletResponse response) {
        if (passengerSigninRequestDto == null) {
            throw new UberAuthException(
                    ErrorCodeEnum.NULL_REQUEST_DTO.getErrorMessage(),
                    ErrorCodeEnum.NULL_REQUEST_DTO.getErrorCode(),
                    HttpStatus.BAD_REQUEST
                );
        }

        try {
            LogMessage.log(logger, "Attempting to sign in passenger: "+ passengerSigninRequestDto.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(passengerSigninRequestDto.getEmail(), 
                                                        passengerSigninRequestDto.getPassword())
            );
            String jwtToken = generateToken(authentication);
            LogMessage.log(logger, "Successfully signed in passenger: "+ passengerSigninRequestDto.getEmail());
            response.addCookie(createJwtCookie(jwtToken));
            return jwtToken;
        } catch (AuthenticationException e) {
            LogMessage.logException(logger, e);
            throw new UberAuthException(
                ErrorCodeEnum.AUTHENTICATION_FAILED.getErrorMessage(),
                ErrorCodeEnum.AUTHENTICATION_FAILED.getErrorCode(),
                HttpStatus.UNAUTHORIZED
            );
        } catch (Exception e) {
            LogMessage.logException(logger, e);
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

    private Cookie createJwtCookie(String jwtToken) {
        Cookie cookie = new Cookie(jwtCookieName, jwtToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(cookieMaxAge);
        cookie.setPath("/");
        return cookie;
    }
}
