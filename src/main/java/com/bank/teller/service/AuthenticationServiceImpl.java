package com.bank.teller.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bank.teller.model.Account;
import com.bank.teller.repository.AccountRepository;
import java.util.Date;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AccountRepository accountRepository;

    @Value("${ignoreAuth}")
    private boolean ignoreAuth;

    Map<Long, String> authDetails = new HashMap<Long, String>();

    public String generateToken(Account account, int count) {
        boolean result = accountRepository.verifyAccount(account);
        if (result == true) {
            
            boolean isStolen = accountRepository.getStolen(account.getAccountNumber());
            
            if(isStolen) 
            {
                return "{\"error\":\"Карта украдена\"}";
            }
            
            boolean isBlock = accountRepository.getBlock(account.getAccountNumber());
            
            if(isBlock) return "{\"error\":\"Карта заблокирована\"}";
            
            Date expirationDate = accountRepository.getExpirationDate(account.getAccountNumber());
            
            Date beginDate = accountRepository.getBeginDate(account.getAccountNumber());

            Date dateNow = new Date();

            if(beginDate.getTime()>dateNow.getTime() || expirationDate.getTime() < dateNow.getTime())
                return "{\"error\":\"Срок действия карты не дейсвителен\"}";
            
            String token = UUID.randomUUID().toString();
            
            authDetails.put(new Long(account.getAccountNumber()), token);
            return "{\"token\":\""+token+"\"}";
        } else if(count ==3) {
            accountRepository.updateAccountCard(account.getAccountNumber());
            return "{\"error\":\""+"Карта не существует или неверный ПИН-код\"}";
        }
        else{
           return "{\"error\":\""+"Карта не существует или неверный ПИН-код\"}";
        }
    }

    @Override
    public boolean verifyToken(long accountNumber, String token) {
        if (ignoreAuth) {
            return true;
        }
        String existingToken = authDetails.get(new Long(accountNumber));

        return(existingToken != null && existingToken.equals(token));
    }

}
