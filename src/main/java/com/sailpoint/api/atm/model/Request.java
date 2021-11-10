package com.sailpoint.api.atm.model;

import lombok.Data;

/**
 * @author Kripashankar
 *
 */
@Data
public class Request {
	private String oldpin;

	private String pin;

	private String value;
}
