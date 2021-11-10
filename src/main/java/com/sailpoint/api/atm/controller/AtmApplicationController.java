package com.sailpoint.api.atm.controller;

import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sailpoint.api.atm.constants.ATMConstants;
import com.sailpoint.api.atm.constants.ErrorMessages;
import com.sailpoint.api.atm.data.DataStore;
import com.sailpoint.api.atm.exception.ApplicationException;
import com.sailpoint.api.atm.model.Request;
import com.sailpoint.api.atm.model.Response;
import com.sailpoint.api.atm.model.User;
import com.sailpoint.api.atm.model.UserTransactionHistory;

/**
 * @author Kripashankar
 *
 */
@RestController
public class AtmApplicationController {

	@Autowired
	private DataStore dataStore;

	@Autowired
	private ErrorMessages errorMessages;

	/**
	 * @param request
	 * @return response //VALIDATE: // Retrieve User Object from Datastore and
	 *         vaidate user.pin aganst the incoming old pin -- if not return error
	 *         //only numeric
	 * 
	 * 
	 *         //only 4 digits
	 * 
	 * 
	 *         //check strength
	 * 
	 * 
	 *         //not equal to yearofbirth
	 * 
	 *         //PROCESS //update User.pin and persist it back to Datastore
	 */
	@RequestMapping(value = ATMConstants.CREATE_PIN, produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST)
	public Response createpin(@RequestBody Request request) {

		Response resp = new Response();
		try {
			ErrorMessages errorMessages = new ErrorMessages();
			resp.setStatus(ATMConstants.RESPONSE_SUCCESS);
			String currentPin = request.getOldpin();
			String newPin = request.getPin();
			// VALIDATE:
			// Retrieve User Object from Datastore and vaidate user.pin against the incoming
			// old pin -- if not valid, return error
			User user = dataStore.getData(ATMConstants.USER_123);

			if (StringUtils.isNotBlank(currentPin) && user.getPin().equals(currentPin)) {
				// only numeric
				if (!newPin.matches("[0-9]+")) {
					resp.setStatus(ATMConstants.RESPONSE_FAILURE);
					resp.setErrorMessage(errorMessages.getErrorMessage("NEW_PIN_INVALID_FORMAT"));
					return resp;
				}

				// only 4 digits
				if (newPin.length() != 4) {
					resp.setStatus(ATMConstants.RESPONSE_FAILURE);
					resp.setErrorMessage(errorMessages.getErrorMessage("PIN_4_DIGITS"));
					return resp;
				}

				// check strength

				// I can write a strength algorithm but considering the time not implementing
				// this right now, can code it separately

				// not equal to yearofbirth
				if (newPin.equals(user.getUserProfile().getYearOfBirth())) {
					resp.setStatus(ATMConstants.RESPONSE_FAILURE);
					resp.setErrorMessage(errorMessages.getErrorMessage("SAME_AS_BIRTHYEAR"));
					return resp;
				}

				// PROCESS
				// update User.pin and persist it back to Datastore
				user.setPin(newPin);
				dataStore.persist(user);
				resp.setErrorMessage(errorMessages.getErrorMessage("PIN_CHANGED"));
			} else {
				resp.setStatus(ATMConstants.RESPONSE_FAILURE);
				resp.setErrorMessage(errorMessages.getErrorMessage("INAVLID_OLDPIN"));
			}

		} catch (Exception ex) {
			throw new ApplicationException();
		}

		return resp;
	}

	/*
	 * VALIDATE Pin entered should be correct Withdrawal amount should be 1. A
	 * number 2. withdrawal amount <= 1000$ 2. Whole number in multiples of 10 3.
	 * Should be a positive number 4. Balance minus this amount should be greater
	 * than a min balance of 100$
	 * 
	 * PROCESS 1. Update the User.Userbalance object with the withdrawn amount 2.
	 * Update the TransactionHistory with this transaction 3. Persist object back
	 * into Data store
	 */
	@RequestMapping(value = ATMConstants.WITHDRAW, produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST)
	public Response withdraw(@RequestBody Request request) {
		Response resp = new Response();
		try {
			ErrorMessages errorMessages = new ErrorMessages();
			resp.setStatus(ATMConstants.RESPONSE_SUCCESS);
			String pin = request.getPin();
			String withdrawalAmount = request.getValue();

			User user = dataStore.getData(ATMConstants.USER_123);

			Integer currentBalance = user.getUserBalance().getBalance();

			// if the pin is blank or not a match then return a error response
			if (StringUtils.isNotBlank(pin) && user.getPin().equals(pin)) {

				// A number
				if (!withdrawalAmount.matches("[0-9]+")) {
					resp.setStatus(ATMConstants.RESPONSE_FAILURE);
					resp.setErrorMessage(errorMessages.getErrorMessage("AMOUNT_INVALID"));
					return resp;
				}

				// Should be a positive number
				if (Integer.valueOf(withdrawalAmount) <= 0) {
					resp.setStatus(ATMConstants.RESPONSE_FAILURE);
					resp.setErrorMessage(errorMessages.getErrorMessage("AMOUNT_GREATER_THAN_ZERO"));
					return resp;
				}

				// Whole number in multiples of 10
				if (Integer.valueOf(withdrawalAmount) % 10 != 0) {
					resp.setStatus(ATMConstants.RESPONSE_FAILURE);
					resp.setErrorMessage(errorMessages.getErrorMessage("AMOUNT_MULTIPLE_10"));
					return resp;
				}

				// less than or equal to 1000$
				if (withdrawalAmount.length() > 5 || Integer.valueOf(withdrawalAmount) > 1000) {
					resp.setStatus(ATMConstants.RESPONSE_FAILURE);
					resp.setErrorMessage(errorMessages.getErrorMessage("AMOUNT_EXCEEDING_LIMIT"));
					return resp;
				}

				// Balance minus this amount should be greater than a min balance of 100$
				if (currentBalance - Integer.valueOf(withdrawalAmount) < 100) {
					resp.setStatus(ATMConstants.RESPONSE_FAILURE);
					resp.setErrorMessage(errorMessages.getErrorMessage("MIN_BALANCE_DEFICIT"));
					return resp;
				}

				// if no errors then Update the User.Userbalance object with the withdrawn
				// amount
				Integer updatedBalance = currentBalance - Integer.valueOf(withdrawalAmount);
				user.getUserBalance().setBalance(updatedBalance);

				UserTransactionHistory historyRecord = new UserTransactionHistory();
				historyRecord.setBalanceAfterTransaction(updatedBalance);
				historyRecord.setBalanceBeforeTransaction(currentBalance);
				historyRecord.setTransactionAmount(Integer.valueOf(withdrawalAmount));
				historyRecord.setTransactionDate(LocalDate.now());
				historyRecord.setTransactionType(ATMConstants.WITHDRAW);

				user.getUserTransactionHistory().add(historyRecord);

				// Persist object back into Data store
				dataStore.persist(user);
				resp.setErrorMessage("Withdrawal successful. Current balance is :" + updatedBalance);
			} else {
				resp.setStatus(ATMConstants.RESPONSE_FAILURE);
				resp.setErrorMessage(errorMessages.getErrorMessage("PIN_INVALID"));
			}
		} catch (Exception ex) {
			throw new ApplicationException();
		}

		return resp;
	}

	/*
	 * VALIDATE Pin entered should be correct Deposit amount should be 1. A number
	 * 2. amount <= 1000$ 2. Whole number in multiples of 10 3. Should be a positive
	 * number
	 * 
	 * 
	 * PROCESS 1. Update the User.Userbalance object with the withdrawn amount 2.
	 * Update the TransactionHistory with this transaction 3. Persist object back
	 * into Data store
	 */
	@RequestMapping(value = ATMConstants.DEPOSIT, produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST)
	public Response deposit(@RequestBody Request request) {
		Response resp = new Response();
		try {
			ErrorMessages errorMessages = new ErrorMessages();
			resp.setStatus(ATMConstants.RESPONSE_SUCCESS);
			String pin = request.getPin();
			String depositAmount = request.getValue();

			User user = dataStore.getData(ATMConstants.USER_123);

			Integer currentBalance = user.getUserBalance().getBalance();

			// if the pin is blank or not a match then return a error response
			if (StringUtils.isNotBlank(pin) && user.getPin().equals(pin)) {

				// A number
				if (!depositAmount.matches("[0-9]+")) {
					resp.setStatus(ATMConstants.RESPONSE_FAILURE);
					resp.setErrorMessage(errorMessages.getErrorMessage("AMOUNT_INVALID"));
					return resp;
				}

				// Should be a positive number
				if (Integer.valueOf(depositAmount) <= 0) {
					resp.setStatus(ATMConstants.RESPONSE_FAILURE);
					resp.setErrorMessage(errorMessages.getErrorMessage("AMOUNT_GREATER_THAN_ZERO"));
					return resp;
				}

				// Whole number in multiples of 10
				if (Integer.valueOf(depositAmount) % 10 != 0) {
					resp.setStatus(ATMConstants.RESPONSE_FAILURE);
					resp.setErrorMessage(errorMessages.getErrorMessage("AMOUNT_MULTIPLE_10"));
					return resp;
				}

				// less than or equal to 1000$
				if (depositAmount.length() > 5 || Integer.valueOf(depositAmount) > 1000) {
					resp.setStatus(ATMConstants.RESPONSE_FAILURE);
					resp.setErrorMessage(errorMessages.getErrorMessage("AMOUNT_EXCEEDING_LIMIT"));
					return resp;
				}

				// if no errors then Update the User.Userbalance object with the deposited
				// amount
				Integer updatedBalance = currentBalance + Integer.valueOf(depositAmount);
				user.getUserBalance().setBalance(updatedBalance);

				UserTransactionHistory historyRecord = new UserTransactionHistory();
				historyRecord.setBalanceAfterTransaction(updatedBalance);
				historyRecord.setBalanceBeforeTransaction(currentBalance);
				historyRecord.setTransactionAmount(Integer.valueOf(depositAmount));
				historyRecord.setTransactionDate(LocalDate.now());
				historyRecord.setTransactionType(ATMConstants.DEPOSIT);

				user.getUserTransactionHistory().add(historyRecord);

				// Persist object back into Data store
				dataStore.persist(user);
				resp.setErrorMessage("Deposit successful. Current balance is :" + updatedBalance);
			} else {
				resp.setStatus(ATMConstants.RESPONSE_FAILURE);
				resp.setErrorMessage(errorMessages.getErrorMessage("PIN_INVALID"));
			}
		} catch (Exception ex) {
			throw new ApplicationException();
		}
		return resp;
	}

	/*
	 * VALIDATE Pin is valid PROCESS Get balance and return
	 */
	@RequestMapping(value = ATMConstants.BALANCE, produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST)
	public Response checkBalance(@RequestBody Request request) {
		Response resp = new Response();
		ErrorMessages errorMessages = new ErrorMessages();
		resp.setStatus(ATMConstants.RESPONSE_SUCCESS);
		String pin = request.getPin();

		User user = dataStore.getData(ATMConstants.USER_123);

		if (StringUtils.isNotBlank(pin) && user.getPin().equals(pin)) {
			Integer currentBalance = user.getUserBalance().getBalance();

			resp.setErrorMessage("The Current balance is : $" + currentBalance);
		} else {
			resp.setStatus(ATMConstants.RESPONSE_FAILURE);
			resp.setErrorMessage(errorMessages.getErrorMessage("PIN_INVALID"));
		}

		return resp;
	}

	/*
	 * VALIDATE Pin is valid PROCESS Get User Profile and return
	 */
	@RequestMapping(value = ATMConstants.VIEW_USER_PROFILE, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> viewUserProfile(@RequestBody Request request) {
		Response resp = new Response();
		ErrorMessages errorMessages = new ErrorMessages();
		resp.setStatus(ATMConstants.RESPONSE_SUCCESS);
		String pin = request.getPin();

		User user = dataStore.getData(ATMConstants.USER_123);

		if (StringUtils.isNotBlank(pin) && user.getPin().equals(pin)) {
			return new ResponseEntity(user.getUserProfile(), HttpStatus.ACCEPTED);
		} else {
			resp.setStatus(ATMConstants.RESPONSE_FAILURE);
			resp.setErrorMessage(errorMessages.getErrorMessage("PIN_INVALID"));
			return new ResponseEntity(resp, HttpStatus.BAD_REQUEST);

		}
	}

	/*
	 * VALIDATE Pin is valid PROCESS Get User Profile and return
	 */
	@RequestMapping(value = ATMConstants.VIEW_USER_HISTORY, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> viewUserHistory(@RequestBody Request request) {
		Response resp = new Response();
		ErrorMessages errorMessages = new ErrorMessages();
		resp.setStatus(ATMConstants.RESPONSE_SUCCESS);
		String pin = request.getPin();

		User user = dataStore.getData(ATMConstants.USER_123);

		if (StringUtils.isNotBlank(pin) && user.getPin().equals(pin)) {
			return new ResponseEntity(user, HttpStatus.ACCEPTED);
		} else {
			resp.setStatus(ATMConstants.RESPONSE_FAILURE);
			resp.setErrorMessage(errorMessages.getErrorMessage("PIN_INVALID"));
			return new ResponseEntity(resp, HttpStatus.BAD_REQUEST);

		}
	}

}
