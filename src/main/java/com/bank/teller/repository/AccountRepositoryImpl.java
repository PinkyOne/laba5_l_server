package com.bank.teller.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bank.teller.model.Account;
import com.bank.teller.util.DBUtil;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private JdbcTemplate template;

    private enum type_Bill{CHECK,SAVING};
    
    private enum type_Operation{WITHDRAWAL,REFIL};
    
    public AccountRepositoryImpl() {
        this.template = DBUtil.getJdbcTemplate();

    }

    @Override
    public double getBalance(long accountNumber,int typeBill) {
        String typeBill_s = type_Bill.values()[typeBill].toString();
        return template.queryForObject("select BALANCE from BANK.BILL where ACCOUNT_NUMBER=? AND TYPES_BILL=?", new Object[] { accountNumber,typeBill_s }, new RowMapper<Double>() {

            public Double mapRow(ResultSet rowSet, int arg1) throws SQLException {

                return rowSet.getDouble("BALANCE");
            }
        });

    }
   
    
    @Override
    public Double getLimit(long accountNumber) {
        return template.queryForObject("select DAILYLIMIT from BANK.ACCOUNT where ACCOUNT_NUMBER=?", new Object[] { accountNumber }, new RowMapper<Double>() {

            public Double mapRow(ResultSet rowSet, int arg1) throws SQLException {

                return rowSet.getDouble("DAILYLIMIT");
            }
        });

    }
    
    @Override
    public boolean getStolen(long accountNumber) {
        return template.queryForObject("select STOLEN from BANK.ACCOUNT where ACCOUNT_NUMBER=?", new Object[] { accountNumber }, new RowMapper<Boolean>() {

            public Boolean mapRow(ResultSet rowSet, int arg1) throws SQLException {

                return rowSet.getBoolean("STOLEN");
            }
        });

    }
    
    @Override
    public boolean getBlock(long accountNumber) {
        return template.queryForObject("select BLOCK from BANK.ACCOUNT where ACCOUNT_NUMBER=?", new Object[] { accountNumber }, new RowMapper<Boolean>() {

            public Boolean mapRow(ResultSet rowSet, int arg1) throws SQLException {

                return rowSet.getBoolean("BLOCK");
            }
        });
    }

    
    @Override
    public Date getExpirationDate(long accountNumber) {
        return template.queryForObject("select EXPIRATION_DATE from BANK.ACCOUNT where ACCOUNT_NUMBER=?", new Object[] { accountNumber }, new RowMapper<Date>() {

            public Date mapRow(ResultSet rowSet, int arg1) throws SQLException {

                return rowSet.getDate("EXPIRATION_DATE");
            }
        });

    }
    
    @Override
    public Date getBeginDate(long accountNumber) {
        return template.queryForObject("select BEGIN_DATE from BANK.ACCOUNT where ACCOUNT_NUMBER=?", new Object[] { accountNumber }, new RowMapper<Date>() {

            public Date mapRow(ResultSet rowSet, int arg1) throws SQLException {

                return rowSet.getDate("BEGIN_DATE");
            }
        });

    }

    @Override
    public boolean withdraw(long accountNumber, int amount,int typeBill) {
        
        String typeBill_s = type_Bill.values()[typeBill].toString();
        
        return template.update("update BANK.BILL set BALANCE=BALANCE-? where ACCOUNT_NUMBER=? AND TYPES_BILL=?", new Object[] { amount, accountNumber, typeBill_s}) == 1;
    }

    @Override
    public boolean transfer(long accountNumber, int amount) {
               
        boolean transferfromcheck = template.update("update BANK.BILL set BALANCE=BALANCE-? where ACCOUNT_NUMBER=? AND TYPES_BILL=?", new Object[] { amount, accountNumber, type_Bill.CHECK.toString()}) == 1;
        
        boolean transfertosaving = template.update("update BANK.BILL set BALANCE=BALANCE+? where ACCOUNT_NUMBER=? AND TYPES_BILL=?", new Object[] { amount, accountNumber, type_Bill.SAVING.toString() }) == 1;
        
        return transferfromcheck&&transfertosaving;
    }

    @Override
    public boolean updateAccountTransaction(long accountNumber, int amount,int typeBill,int typeOperation) {       
        
        String typeBill_s = type_Bill.values()[typeBill].toString();
        
        String typeOperation_s = type_Operation.values()[typeOperation].toString();
        
        return template.update("insert into BANK.TRANSACTION_DETAILS(ACCOUNT_NUMBER,TRANSACTION_TIME,TRANSACTION_AMOUNT,TYPES_OPERATION,TYPES_BILL) values(?,?,?,?,?)", new Object[] { accountNumber, new Date(), amount,typeOperation_s,typeBill_s }) == 1;
    }

    @Override
    public boolean updateAccountCard(long accountNumber) {       
        
        
        return template.update("update BANK.ACCOUNT set BLOCK=? where ACCOUNT_NUMBER=?", new Object[] { true,accountNumber}) == 1;
    }

    @Override
    public boolean verifyBill(long accountNumber,int typeBill) {
        String typeBill_s = type_Bill.values()[typeBill].toString();
        return template.queryForObject("select count(*) as ACCOUNT_COUNT from BANK.BILL where ACCOUNT_NUMBER=? AND TYPES_BILL=?", new Object[] { accountNumber, typeBill_s }, new RowMapper<Integer>() {

            public Integer mapRow(ResultSet rowSet, int arg1) throws SQLException {

                return rowSet.getInt("ACCOUNT_COUNT");
            }
        }) == 1;

    }
    
    @Override
    public boolean verifyAccount(Account account) {
        return template.queryForObject("select count(*) as ACCOUNT_COUNT from BANK.ACCOUNT where ACCOUNT_NUMBER=? AND ACCOUNT_NAME=? AND PIN=?", new Object[] { account.getAccountNumber(), account.getAccountName(),
                account.getPin() }, new RowMapper<Integer>() {

            public Integer mapRow(ResultSet rowSet, int arg1) throws SQLException {

                return rowSet.getInt("ACCOUNT_COUNT");
            }
        }) == 1;

    }
    
    @Override
    public Double verifyLimit(long accountNumber,int typeBill) {
        
       String typeBill_s = type_Bill.values()[typeBill].toString();
       
       
       Date today = new Date();
       today.setHours(0);
       today.setMinutes(0);
       today.setSeconds(0);

        
        Date tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate()+1);
        tomorrow.setHours(0);
        tomorrow.setMinutes(0);
        tomorrow.setSeconds(0);
    
        return template.queryForObject("select sum(TRANSACTION_AMOUNT) as SUM_LIMIT from BANK.TRANSACTION_DETAILS where ACCOUNT_NUMBER=? AND TRANSACTION_TIME>? AND TRANSACTION_TIME<? AND TYPES_OPERATION=? AND TYPES_BILL=?", new Object[] { 
        accountNumber,today,tomorrow,type_Operation.WITHDRAWAL.toString(),typeBill_s}, new RowMapper<Double>() {

            public Double mapRow(ResultSet rowSet, int arg1) throws SQLException {

                return rowSet.getDouble("SUM_LIMIT");
            }
        });
        
        

    }
}
