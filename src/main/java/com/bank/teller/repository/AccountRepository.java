package com.bank.teller.repository;

import com.bank.teller.model.Account;
import java.util.Date;

public interface AccountRepository {

	double getBalance(long accountNumber,int typeBill);

	boolean withdraw(long accountNumber, int amount,int typeBill);
        
        boolean transfer(long accountNumber, int amount);

	boolean updateAccountTransaction(long accountNumber, int amount,int typeBill,int typeOperation);

        boolean verifyAccount(Account account);
        
        boolean getStolen(long accountNumber);
        
        boolean getBlock(long accountNumber);
         
        Date getExpirationDate(long accountNumber);
        
        Date getBeginDate(long accountNumber);
        
        boolean verifyBill(long accountNumber,int typeBill);
        
        Double verifyLimit(long accountNumber,int typeBill);
        
        Double getLimit(long accountNumber);
        
        boolean updateAccountCard(long accountNumber);
        
}
