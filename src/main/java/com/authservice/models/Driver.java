package com.authservice.models;

import java.util.List;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "bookings" })
public class Driver extends BaseModel {
	private String name;
	@Column(nullable = false, unique = true)
	private String licenseNumber;
	private String phoneNumber;
	// 1 : n , Driver : Booking
	@OneToMany(mappedBy = "driver")
	@Fetch(FetchMode.SUBSELECT)
	private List<Booking> bookings;
}