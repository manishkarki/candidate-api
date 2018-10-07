package com.hubspot.candidate.hubspotapi.entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Partner {
	
	private String firstName;

	private String lastName;

	private String email;

	private String country;
	
	private List<Date> availableDates;
	
	public Partner() {
		availableDates = new ArrayList<>();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<Date> getAvailableDates() {
		return availableDates;
	}

	public void setAvailableDates(List<Date> availableDates) {
		this.availableDates = availableDates;
	}

	@Override
	public String toString() {
		return "Partners [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", country="
				+ country + ", availableDates=" + availableDates + "]";
	}
	
	
}
