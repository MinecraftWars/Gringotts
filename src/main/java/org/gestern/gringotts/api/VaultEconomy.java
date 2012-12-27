package org.gestern.gringotts.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.gestern.gringotts.Account;
import org.gestern.gringotts.Configuration;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.Util;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.currency.Currency;

/** 
 * Provides the vault interface, so that the economy adapter in vault does not need to be changed. 
 * 
 * @author jast
 *
 */
public class VaultEconomy implements Economy {

    @SuppressWarnings("unused")
    private static final Logger log = Gringotts.gringotts.getLogger();

    private final String name = "Gringotts";
    private final Gringotts gringotts;
    private final Currency currency;

    public VaultEconomy(Gringotts gringotts) {
    	this.gringotts = gringotts;
    	this.currency = Configuration.config.currency;
    }


    @Override
    public boolean isEnabled(){
        return gringotts != null && gringotts.isEnabled();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasBankSupport(){
        return false;
    }

    @Override
    public int fractionalDigits(){
        return 2;
    }

    @Override
    public String format(double amount) {
        return Util.format(amount);
    }

    @Override
    public String currencyNamePlural(){
        return org.gestern.gringotts.Configuration.config.currency.namePlural;
    }

    @Override
    public String currencyNameSingular(){
        return org.gestern.gringotts.Configuration.config.currency.name;
    }

    @Override
    public boolean hasAccount(String playerName) {
        AccountHolder owner = gringotts.accountHolderFactory.get(playerName);
        if (owner == null) return false;

        return gringotts.accounting.getAccount(owner) != null;
    }

    @Override
    public double getBalance(String playerName){
        AccountHolder owner = gringotts.accountHolderFactory.get(playerName);
        if (owner == null) return 0;
        Account account = gringotts.accounting.getAccount(owner);
        return currency.displayValue(account.balance());
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {

        if( amount < 0 ) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw a negative amount.");
        }

        AccountHolder accountHolder = gringotts.accountHolderFactory.get(playerName);
        if (accountHolder == null) 
            return new EconomyResponse(0, 0, ResponseType.FAILURE, playerName + " is not a valid account holder.");

        Account account = gringotts.accounting.getAccount( accountHolder );

        
        TransactionResult removed = account.remove(currency.centValue(amount));
        
        if (removed==TransactionResult.SUCCESS)
        	return new EconomyResponse(amount, currency.displayValue(account.balance()), ResponseType.SUCCESS, null);
        else if (removed == TransactionResult.INSUFFICIENT_FUNDS)
            return new EconomyResponse(0, currency.displayValue(account.balance()), ResponseType.FAILURE, "Insufficient funds");
        else
        	return new EconomyResponse(0, currency.displayValue(account.balance()), ResponseType.FAILURE, "Negative amount or other error.");

    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount){
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds");
        }

        AccountHolder accountHolder = gringotts.accountHolderFactory.get(playerName);
        if (accountHolder == null) 
            return new EconomyResponse(0, 0, ResponseType.FAILURE, playerName + " is not a valid account holder.");

        Account account = gringotts.accounting.getAccount( accountHolder );

        TransactionResult added = account.add(currency.centValue(amount));
        if (added==TransactionResult.SUCCESS)
            return new EconomyResponse( amount, currency.displayValue(account.balance()), ResponseType.SUCCESS, null);
        else if (added == TransactionResult.INSUFFICIENT_SPACE)
            return new EconomyResponse(0, currency.displayValue(account.balance()), ResponseType.FAILURE, "Not enough capacity to store that amount!");
        else
        	return new EconomyResponse(0, currency.displayValue(account.balance()), ResponseType.FAILURE, "Negative amount or other error.");
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support bank accounts!");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<String>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return hasAccount(playerName);
    }

}
