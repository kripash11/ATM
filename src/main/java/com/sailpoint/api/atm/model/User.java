/**
 * author: Kripashankar
 */
package com.sailpoint.api.atm.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data

public class User {
	
	private UserBalance userBalance ;
	
	private UserProfile userProfile ;
	
	private List<UserTransactionHistory> userTransactionHistory = new ArrayList<>();
	
	private String pin; 

}
