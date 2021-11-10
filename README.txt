This ATM application has 6 capabilities,

1. Create PIN
2. Withdraw money
3. Deposit money
4. View User Profile
5. View User History
6. Check Balance

***Assumptions/*** 
1. This app works on 1 user.
2. This application assumes the User is already setup and doesnt provide the ability to update userinfo. (can view the user profile though)
3. User Profile details are assumed during server startup
4. A Default PIN is assigned during start up -  4735. This pin is needed to update the PIN to a different value
5. PIN is not encrypted considering this is a 2 hour exercise.
6. NO DB is used to store the data.. all txns happen in a in memory Hashmap (Datastore). So when the server restarts, all the details are reset.


1. Create PIN
Endpoint:localhost:8080/createpin

Steps:
 //VALIDATE:
			//	Retrieve User Object from Datastore and vaidate user.pin aganst the incoming old pin -- if not return error
			//only numeric
		
		
			//only 4 digits
		
		
			//check strength
		
		
			//not equal to yearofbirth
			 * 
	 * //PROCESS
		//update User.pin and persist it back to Datastore
		
2. Withdraw money
Endpoint:localhost:8080/withdraw

Steps:
/*VALIDATE
	 * Pin entered should be correct
	 * Withdrawal amount should be
	 * 1. A number
	 * 2. withdrawal amount <= 1000$ 
	 * 2. Whole number in multiples of 10
	 * 3. Should be a positive number
	 * 4. Balance minus this amount should be greater than a min balance of 100$
	 * 
	 * PROCESS
	 * 1. Update the User.Userbalance object with the withdrawn amount
	 * 2. Update the TransactionHistory with this transaction
	 * 3. Persist object back into Data store
	 */
	 
	 
3. Deposit
Endpoint:localhost:8080/deposit

Steps:
	/*VALIDATE
	 * Pin entered should be correct
	 * Deposit amount should be
	 * 1. A number
	 * 2. amount <= 1000$ 
	 * 2. Whole number in multiples of 10
	 * 3. Should be a positive number
	 * 
	 * 
	 * PROCESS
	 * 1. Update the User.Userbalance object with the withdrawn amount
	 * 2. Update the TransactionHistory with this transaction
	 * 3. Persist object back into Data store
	 */
	 

4. View User Profile
Endpoint:localhost:8080/userprofile

Steps:
/*
	 * VALIDATE
	 * 	Pin is valid
	 * PROCESS
	 * 	Get User Profile and return
	 */
	 
5. View User History
Endpoint:localhost:8080/userhistory

Steps:
/*
	 * VALIDATE
	 * 	Pin is valid
	 * PROCESS
	 * 	Get User Profile and return
	 */
	 
	 
	 
6. Check Balance
Endpoint:localhost:8080/balancecheck

Steps:

	/*
	 * VALIDATE
	 * 	Pin is valid
	 * PROCESS
	 * 	Get balance and return
	 */