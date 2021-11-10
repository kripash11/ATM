package com.sailpoint.api.atm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Kripashankar
 *
 */
@ControllerAdvice
public class ExceptionController {
	
	@ExceptionHandler(value = ApplicationException.class)
	public ResponseEntity<Object> exception(ApplicationException ex){
		return new ResponseEntity<>("ATM app  is down", HttpStatus.NOT_FOUND);
	}
	

}
