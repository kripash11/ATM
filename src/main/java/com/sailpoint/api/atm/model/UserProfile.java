package com.sailpoint.api.atm.model;

import lombok.Data;

@Data
public class UserProfile {

	private String firstName;
	
	private String lastName;
	
	private String yearOfBirth;
	
	private String address1;
	
	private String city;
	
	private String state;
	
	private String zip;
}
