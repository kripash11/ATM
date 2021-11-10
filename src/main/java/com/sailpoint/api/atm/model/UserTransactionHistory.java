package com.sailpoint.api.atm.model;

import java.time.LocalDate;
import java.util.Date;

import lombok.Data;

/**
 * @author Kripashankar
 *
 */
@Data
public class UserTransactionHistory {
	
	private LocalDate transactionDate;
	
	private String transactionType;
	
	private Integer transactionAmount;
	
	private Integer balanceAfterTransaction;
	
	private Integer balanceBeforeTransaction;

}
