package com.sailpoint.api.atm.constants;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author Kripashankar
 *
 */
@Data
@Component
public class ErrorMessages {

	/*
	 * INAVLID_OLDPIN("Old pin entered is Invalid"),
	 * NEW_PIN_INVALID_FORMAT("Invalid Pin value entered"),
	 * PIN_NOT_STRONG("Please enter a stronger pin");
	 * 
	 * public final String label;
	 * 
	 * private ErrorMessages(String label) { this.label = label; }
	 * 
	 * public ErrorMessages getErrorMessage(String key) { return valueOf(key);
	 * 
	 * }
	 */
	
	HashMap<String, String> errorMap= new HashMap<>();
	
	public ErrorMessages() {
		errorMap.put("INAVLID_OLDPIN", "Current pin entered is Invalid");
		errorMap.put("NEW_PIN_INVALID_FORMAT", "Invalid New Pin value entered");
		errorMap.put("PIN_NOT_STRONG", "Please enter a stronger pin");
		errorMap.put("SAME_AS_BIRTHYEAR", "Old pin entered cannot be same as Birth year");
		errorMap.put("PIN_4_DIGITS", "Pin should be 4 digits");
		errorMap.put("PIN_CHANGED", "Pin changed successfully");
		errorMap.put("PIN_INVALID", "Pin entered is Invalid");
		errorMap.put("AMOUNT_INVALID", "Amount entered is Invalid");
		errorMap.put("AMOUNT_EXCEEDING_LIMIT", "Amount entered cannot exceed the withdrawal limit of 1000 $");
		errorMap.put("AMOUNT_GREATER_THAN_ZERO", "Amount entered should be greater than 0");
		errorMap.put("AMOUNT_MULTIPLE_10", "Amount entered should be in multiples of 10");
		errorMap.put("MIN_BALANCE_DEFICIT", "Min balance of 100$ is required. Please change the Withdrawal Amount");


	}
	
	public  String getErrorMessage(String key) {
		return errorMap.get(key);
	}
}
