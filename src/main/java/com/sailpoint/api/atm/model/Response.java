/**
 * author: Kripashankar
 */
package com.sailpoint.api.atm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class Response {

	private String status;
	
	private String errorMessage;
}
