package com.authservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entityservice.models.Passenger;


public interface PassengerRepository extends JpaRepository<Passenger, Long> {
	Optional<Passenger> findPassengerByEmail(String email);
}