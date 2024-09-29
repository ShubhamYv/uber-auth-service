package com.authservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.authservice.models.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {
}