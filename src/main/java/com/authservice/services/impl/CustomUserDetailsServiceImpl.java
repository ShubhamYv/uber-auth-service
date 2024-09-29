package com.authservice.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.authservice.models.Passenger;
import com.authservice.repositories.PassengerRepository;
import com.authservice.services.CustomUserDetailsService;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final PassengerRepository passengerRepository;

    public CustomUserDetailsServiceImpl(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Passenger> passengerOpt = passengerRepository.findPassengerByEmail(email);
        if (!passengerOpt.isPresent()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        
        Passenger passenger = passengerOpt.get();
        List<GrantedAuthority> authorities = new ArrayList<>();

        return new org.springframework.security.core.userdetails.User(
                passenger.getEmail(),
                passenger.getPassword(),
                authorities
        );
    }
}
