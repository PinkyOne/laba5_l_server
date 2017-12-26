package com.bank.teller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.teller.repository.AccountRepository;

@Service
public class TellerServiceImpl implements TellerService {

    @Autowired
    private AccountRepository accountRepository;

   
    public String checkBalance(long accountNumber) {
    
        double balance_check = accountRepository.getBalance(accountNumber,0);
        Double balance_saving = null;
        try{
            balance_saving = accountRepository.getBalance(accountNumber,1);
        }catch(Exception e){
            return "{\"accountNumber\":\""+accountNumber+"\"," +
                "\"balanceCheck\":\""+balance_check+"\"}";
        }
        
        return "{\"accountNumber\":\""+accountNumber+"\"," +
                "\"balanceCheck\":\""+balance_check+"\"," +
                "\"balanceSaving\":\""+balance_saving+"\"}";
    }
    

    
    @Transactional
    public boolean withdraw(long accountNumber, int amount,int typeBill) throws Exception {
        
        boolean exsistsBill = accountRepository.verifyBill(accountNumber,typeBill); 
        
        if (exsistsBill) {
            double balance = accountRepository.getBalance(accountNumber, typeBill);
            if (balance >= amount) {

                double limit = accountRepository.getLimit(accountNumber);

                double now_limit = accountRepository.verifyLimit(accountNumber, typeBill);

                if (Math.abs(now_limit) + amount <= Math.abs(limit)) {

                    boolean result = accountRepository.withdraw(accountNumber, amount, typeBill);
                    if (result) {
                        result = accountRepository.updateAccountTransaction(accountNumber, -amount, 0, 0);
                        return result;
                    } else {
                        return false;
                    }
                } else {
                    throw new Exception("Превышен суточный лимит");
                }
            } else {
                throw new Exception("Недостаточно средств для снятия");
            }
        } else {
            throw new Exception("У вас нет данного банковского счета.");
        }
    }
    
    @Transactional
    public boolean transfer(long accountNumber, int amount) throws Exception {
       
        
        boolean exsistsBillCheck = accountRepository.verifyBill(accountNumber,0); 
        boolean exsistsBillSaving = accountRepository.verifyBill(accountNumber,1); 
        
        if (exsistsBillCheck && exsistsBillSaving) {
            double balanceCheck = accountRepository.getBalance(accountNumber, 0);
            double balanceSaving = accountRepository.getBalance(accountNumber, 1);

            if (balanceCheck >= amount) {
                boolean result = true;
                result = accountRepository.transfer(accountNumber, amount);//accountRepository.withdraw(accountNumber, amount,typeBill);
                if (result) {
                    
                    boolean resultW = accountRepository.updateAccountTransaction(accountNumber, -amount,0,0);
                    
                    boolean resultR = accountRepository.updateAccountTransaction(accountNumber, amount,1,1);
                    
                    return resultW&&resultR;
                } else {
                    return false;
                }
            } else {
                throw new Exception("Недостаточно средств для перевода.");
            }
        } else {
            throw new Exception("У вас нет данного банковского счета.");
        }
    }

}
