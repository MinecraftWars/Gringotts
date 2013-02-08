package org.gestern.gringotts.api.impl;

import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.api.Account;
import org.gestern.gringotts.api.Eco;
import org.gestern.gringotts.api.TransactionResult;

/** 
 * Provides the vault interface, so that the economy adapter in vault does not need to be changed. 
 * 
 * @author jast
 *
 */
public class VaultConnector implements Economy {

    private final String name = "Gringotts";
    private final Eco eco = new GringottsEco();

    public VaultConnector() {
    }


    @Override
    public boolean isEnabled(){
        return Gringotts.G != null && Gringotts.G.isEnabled();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return eco.currency().fractionalDigits();
    }

    @Override
    public String format(double amount) {
        return eco.currency().format(amount);
    }

    @Override
    public String currencyNamePlural(){
    	return eco.currency().namePlural();
    }

    @Override
    public String currencyNameSingular(){
    	return eco.currency().name();
    }

    @Override
    public boolean hasAccount(String playerName) {
    	return eco.account(playerName).exists();
    }

    @Override
    public double getBalance(String playerName){
    	return eco.account(playerName).balance();
    }

    @Override
    public boolean has(String playerName, double amount) {
    	return eco.account(playerName).has(amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {

    	Account account = eco.account(playerName);
    	TransactionResult removed = account.remove(amount);
    	
    	switch (removed) {
	    	case SUCCESS:
	    		return new EconomyResponse(amount, account.balance(), ResponseType.SUCCESS, null);
	    	case INSUFFICIENT_FUNDS:
	    		return new EconomyResponse(0, account.balance(), ResponseType.FAILURE, "Insufficient funds.");
	    	case ERROR:
	    	default: 
	    		return new EconomyResponse(0, account.balance(), ResponseType.FAILURE, "Unknown failure.");
    	}
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount){

    	Account account = eco.account(playerName);
    	TransactionResult added = account.add(amount);
    	
    	switch (added) {
	    	case SUCCESS:
	    		return new EconomyResponse(amount, account.balance(), ResponseType.SUCCESS, null);
	    	case INSUFFICIENT_SPACE:
	    		return new EconomyResponse(0, account.balance(), ResponseType.FAILURE, "Insufficient space.");
	    	case ERROR:
	    	default: 
	    		return new EconomyResponse(0, account.balance(), ResponseType.FAILURE, "Unknown failure.");
    	}
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
    	return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support banks");
//        BankAccount bank = eco.bank(name).addOwner(player);
//        if (bank.exists())
//        	return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to create bank!");
//        else
//        	return new EconomyResponse(0, 0, ResponseType.SUCCESS, "Created bank " + name);
    }

    @Override
    public EconomyResponse deleteBank(String name) {
    	return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support banks");
//    	Account deleted = eco.bank(name).delete();
//    	if (deleted.exists())
//    		return new EconomyResponse(0, 0, ResponseType.FAILURE, "Unable to delete bank account!");
//    	else
//    		return new EconomyResponse(0, 0, ResponseType.SUCCESS, "Deleted bank account (or it didn't exist)");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
    	return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support banks");
//    	double balance = eco.bank(name).balance();
//        return new EconomyResponse(0, balance, 
//        		ResponseType.SUCCESS, "Balance of bank "+ name +": "+ balance);
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
    	return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support banks");
//    	BankAccount bank = eco.bank(name);
//    	double balance = bank.balance();
//    	if (bank.has(amount))
//    		return new EconomyResponse(0, balance, ResponseType.SUCCESS, "Bank " + name + " has at least " + amount );
//    	else
//    		return new EconomyResponse(0, balance, ResponseType.FAILURE, "Bank " + name + " does not have at least " + amount );
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
    	return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support banks");
//    	BankAccount bank = eco.bank(name);
//    	TransactionResult result = bank.remove(amount);
//    	if (result == TransactionResult.SUCCESS)
//    		return new EconomyResponse(amount, bank.balance(), ResponseType.SUCCESS, "Removed " + amount + " from bank " + name);
//    	else 
//    		return new EconomyResponse(0, bank.balance(), ResponseType.SUCCESS, "Failed to remove " + amount + " from bank " + name);
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
    	return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support banks");
//    	BankAccount bank = eco.bank(name);
//    	TransactionResult result = bank.add(amount);
//    	if (result == TransactionResult.SUCCESS)
//    		return new EconomyResponse(amount, bank.balance(), ResponseType.SUCCESS, "Added " + amount + " to bank " + name);
//    	else 
//    		return new EconomyResponse(0, bank.balance(), ResponseType.SUCCESS, "Failed to add " + amount + " to bank " + name);
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
    	return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support banks");
//        return new EconomyResponse(0, 0, eco.bank(name).isOwner(playerName)? ResponseType.SUCCESS : FAILURE, "");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
    	return new EconomyResponse(0,0, ResponseType.NOT_IMPLEMENTED, "Gringotts does not support banks");
//    	return new EconomyResponse(0, 0, eco.bank(name).isMember(playerName)? ResponseType.SUCCESS : FAILURE, "");
    }

    @Override
    public List<String> getBanks() {
    	return new ArrayList<String>();
//        return new ArrayList<String>(eco.getBanks());
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return hasAccount(playerName);
    }

}
