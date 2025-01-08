package com.authservice.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.authservice.repositories.PassengerRepository;
import com.authservice.services.CustomUserDetailsService;
import com.entityservice.models.Passenger;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private static final Logger LOGGER = LogManager.getLogger(CustomUserDetailsServiceImpl.class);

    private final PassengerRepository passengerRepository;

    public CustomUserDetailsServiceImpl(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.info("Attempting to load user by email: {}", email);

        Optional<Passenger> passengerOpt = passengerRepository.findPassengerByEmail(email);
        
        if (!passengerOpt.isPresent()) {
            LOGGER.error("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        
        Passenger passenger = passengerOpt.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        LOGGER.info("User found: {}", passenger.getEmail());

        return new org.springframework.security.core.userdetails.User(
                passenger.getEmail(),
                passenger.getPassword(),
                authorities
        );
    }
}