package com.bank.teller.service;

public interface TellerService {

	//double checkBalance(long accountNumber);

        String checkBalance(long accountNumber);
        
	boolean withdraw(long accountNumber, int amount,int typeBill) throws Exception;
        
        boolean transfer(long accountNumber, int amount) throws Exception;


}
