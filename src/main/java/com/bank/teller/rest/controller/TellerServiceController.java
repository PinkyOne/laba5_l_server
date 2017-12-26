package com.bank.teller.rest.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bank.teller.model.Account;
import com.bank.teller.service.AuthenticationService;
import com.bank.teller.service.TellerService;

@Controller
@RequestMapping("/teller")
public class TellerServiceController {

    @Autowired
    private TellerService tellerService;

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(
            value = "/gettoken/{count}",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/json; charset=utf-8")
    public @ResponseBody String authorize(@RequestBody @Valid Account account,@PathVariable(value = "count") int count) {
        return authenticationService.generateToken(account,count);
    }

    @RequestMapping(value = "/withdraw/{token}/{accountnumber}/{amount}/{typeBill}", produces = "application/json; charset=utf-8")
    public @ResponseBody String withdraw(@PathVariable(value = "token") String token, @PathVariable(value = "accountnumber") long accountNumber, @PathVariable(value = "amount") int amount,@PathVariable(value = "typeBill") int typeBill) {
        try {
            if (authenticationService.verifyToken(accountNumber, token)) {
                if (tellerService.withdraw(accountNumber, amount,typeBill)) {
                    return "{\"data\":\"Снятие наличных завершено успешно\"}";
                } else {
                    return "{\"error\":\"Ошибка во время выполнения транзакции\"}";
                }
            } else {
                return "{\"error\":\"Доступ заблокирован\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Ошибка во время выполнения транзакции: "+e.getMessage()+"\"}";
        }
    }

     @RequestMapping(value = "/transfer/{token}/{accountnumber}/{amount}", produces = "application/json; charset=utf-8")
    public @ResponseBody String transfer(@PathVariable(value = "token") String token, @PathVariable(value = "accountnumber") long accountNumber, @PathVariable(value = "amount") int amount) {
        try {
            if (authenticationService.verifyToken(accountNumber, token)) {
                if (tellerService.transfer(accountNumber, amount)) {
                    return "{\"data\":\"Перевод средств успешно завершен\"}";
                } else {
                    return "{\"error\":\"Ошибка во время выполнения транзакции\"}";
                }
            } else {
                return "{\"error\":\"Доступ заблокирован\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Ошибка во время выполнения транзакции: "+e.getMessage()+"\"}";
        }
    }
  
    @RequestMapping(value = "/checkbalance/{token}/{accountnumber}", produces = "application/json; charset=utf-8")
    public @ResponseBody String checkBalance(@PathVariable(value = "token") String token, @PathVariable(value = "accountnumber") long accountNumber) {
        try {
            if (authenticationService.verifyToken(accountNumber, token)) {
                String balance = tellerService.checkBalance(accountNumber);
                return balance;
            } else {
                return "{\"error\":\"Ошибка во время выполнения транзакции\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Ошибка во время выполнения транзакции: "+e.getMessage()+"\"}";
        }
    }

}
