package com.sailpoint.api.atm.data;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.sailpoint.api.atm.constants.ATMConstants;
import com.sailpoint.api.atm.model.User;
import com.sailpoint.api.atm.model.UserBalance;
import com.sailpoint.api.atm.model.UserProfile;

import lombok.Data;

/**
 * @author Kripashankar
 * This is a Datastore that instantiates the Map with UserProfile
 *
 */
@Service
@Data
public class DataStore {

	public DataStore(){
		User user = new User();
		UserProfile prof = new UserProfile();
		
		prof.setFirstName("Kane");
		prof.setLastName("Williamson");
		prof.setState("OHIO");
		prof.setAddress1("3714 Lockson Ln");
		prof.setYearOfBirth("1982");
		prof.setCity("Cincinnati");
		prof.setZip("45040");
		
		user.setPin("4735");
		user.setUserProfile(prof);
		
		UserBalance balance = new UserBalance();
		balance.setBalance(200);
		
		user.setUserBalance(balance);
		
		userMap.put(ATMConstants.USER_123, user);
	}
	private HashMap<String, User>userMap = new HashMap<>();
	
	
	public User getData(String user) {
		
		return userMap.get(user);
	}


	public void persist(User user) {
		userMap.put(ATMConstants.USER_123, user);
		
	}

}
