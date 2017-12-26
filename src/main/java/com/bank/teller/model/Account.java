package com.bank.teller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

public class Account {
    @JsonProperty("accountName")
    private String accountName;
    
    @JsonProperty("accountNumber")
    private long accountNumber;
    
    @JsonProperty("pin")
    private int pin;
    
    
   /* @JsonProperty("id_bill")
    private int id_bill;*/
    /*@JsonProperty("balance")
    private double balance;*/

    /*@JsonProperty("stolen")
    private boolean stolen;
    
    private Date expirationDate;*/

    
    public String getAccountName() {
        return accountName;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public int getPin() {
        return pin;
    }

    /*public double getBalance() {
        return balance;
    }
    
    public boolean getStolen() {
        return stolen;
    }*/
}
