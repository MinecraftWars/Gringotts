package org.gestern.gringotts;

import static org.gestern.gringotts.TransactionResult.*;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.PlayerAccountHolder;

public class Account {

    @SuppressWarnings("unused")
	private final Logger log = Gringotts.gringotts.getLogger();
    private final DAO dao = DAO.getDao(); 

    public final AccountHolder owner;
    
	private final Configuration config = Configuration.config;

    public Account(AccountHolder owner) {
    	if (owner == null) {
    		throw new IllegalArgumentException("owner parameter to Account constructor may not be null");
    	}
        this.owner = owner;
    }

    /**
     * Current balance of this account in cents
     * @return current balance of this account in cents
     */
    long balanceCents() {
        long balance = 0;
        
        if (config.usevaultContainer) {
	        for (AccountChest chest : dao.getChests(this))
	            balance += chest.balance();
        }
        
        Player player = playerOwner();
        if (player != null) {
        	if (player.hasPermission("gringotts.usevault.inventory"))
        		balance += new AccountInventory(player.getInventory()).balance();
        	if (Configuration.config.usevaultEnderchest && player.hasPermission("gringotts.usevault.enderchest"))
        		balance += new AccountInventory(player.getEnderChest()).balance();
        }

        // convert to total cents
        return Util.toCents(balance) + (config.currencyFractional? dao.getCents(this) : 0);
    }

    /**
     * Current balance of this account.
     * @return
     */
    public double balance() {
        return Util.toEmeralds( balanceCents() );
    }

    /**
     * Add an amount in cents to this account if able to.
     * @param amount
     * @return Whether amount successfully added
     */
    TransactionResult addCents(long amount) {

        // Cannot add negative amount
        if(amount < 0)
            return ERROR;

        // Add the cents
        long cents = amount;
        long remainingEmeralds = 0;
        if (config.currencyFractional) {
	        cents += dao.getCents(this); 
	
	        // Convert excess cents into emeralds
	        while(cents >= 100) {
	            cents -= 100;
	            remainingEmeralds += 1;
	        }
	        
	        dao.storeCents(this, (int)cents); // FIXME don't store cents before it's all over
    	} else {
    		remainingEmeralds = cents/100;
    	}

        if (config.usevaultContainer) {
	        for (AccountChest chest : dao.getChests(this)) {
	            remainingEmeralds -= chest.add(remainingEmeralds);
	            if (remainingEmeralds <= 0) break;
	        }
        }
        
        // add stuff to player's inventory and enderchest too, when they are online
        Player player = playerOwner();
        if (player != null) {
        	if (player.hasPermission("gringotts.usevault.inventory"))
        		remainingEmeralds -= new AccountInventory(player.getInventory()).add(remainingEmeralds);
        	if (Configuration.config.usevaultEnderchest && player.hasPermission("gringotts.usevault.enderchest"))
        		remainingEmeralds -= new AccountInventory(player.getEnderChest()).add(remainingEmeralds);
        }
        
        if (remainingEmeralds == 0) 
        	return SUCCESS;
        
        // failed, remove the stuff added so far
        removeCents(amount-remainingEmeralds);
        return INSUFFICIENT_SPACE;
    }

    /**
     * Add an amount to this account if able to.
     * @param amount
     * @return Whether amount successfully added
     */
    public TransactionResult add(double amount) {
        return addCents(Util.toCents(amount));
    }

    /**
     * Attempt to remove an amount in cents from this account. 
     * If the account contains less than the specified amount, returns false
     * @param amount
     * @return amount actually removed.
     */
    TransactionResult removeCents(long amount) {

        //Cannot remove negative amount
        if(amount < 0)
            return ERROR;

        //Make sure we have enough to remove
        if(balanceCents() < amount)
            return INSUFFICIENT_FUNDS;

        long cents = 0;
        long remainingEmeralds = 0;
        if(config.currencyFractional) {
	        //Remove the cents
	        cents = dao.getCents(this) - amount;
	
	        //Now lets get our amount of cents positive again, and count how many emeralds need removing
	        while(cents < 0) {
	            cents += 100;
	            remainingEmeralds += 1;
	        }
	        
	        dao.storeCents(this, (int)cents);
        } else {
        	remainingEmeralds = amount/100;
        }

        //Now remove the physical amount left
        if (config.usevaultContainer) {
	        for (AccountChest chest : dao.getChests(this)) {
	            remainingEmeralds -= chest.remove(remainingEmeralds);
	            if (remainingEmeralds <= 0) break;
	        }
        }
        
        Player player = playerOwner();
        if (player != null) {
        	if (player.hasPermission("gringotts.usevault.inventory"))
        		remainingEmeralds -= new AccountInventory(player.getInventory()).remove(remainingEmeralds);
        	if (Configuration.config.usevaultEnderchest && player.hasPermission("gringotts.usevault.enderchest"))
        		remainingEmeralds -= new AccountInventory(player.getEnderChest()).remove(remainingEmeralds);
        }

        return TransactionResult.SUCCESS;
    }

    /**
     * Attempt to remove an amount from this account. 
     * If the account contains less than the specified amount, returns false
     * @param amount
     * @return amount actually removed.
     */
    public TransactionResult remove(double amount) {
        return removeCents(Util.toCents(amount));
    }

    /**
     * Attempt to transfer an amount of currency to another account. 
     * If the transfer fails because of insufficient funds, both accounts remain at previous
     * balance, and false is returned.
     * @param value amount to transfer
     * @param other account to transfer funds to.
     * @return false if this account had insufficient funds.
     */
    public TransactionResult transfer(double value, Account other) {

        // First try to deduct the amount from this account
    	TransactionResult removed = this.remove(value);
        if(removed == SUCCESS) {
            // Okay, now lets send it to the other account
        	TransactionResult added = other.add(value);
            if(added!=SUCCESS) {
                // Oops, failed, better refund this account
                this.add(value);
            }
            return added;
        } 
        return removed;
    }
    
    @Override
    public String toString() {
    	return "Account ("+owner+")";
    }
    
    /**
     * Returns the player owning this account, if the owner is actually a player and online.
     * @return the player owning this account, if the owner is actually a player and online, otherwise null
     */
    private Player playerOwner() {
    	if (owner instanceof PlayerAccountHolder) {
        	OfflinePlayer player = ((PlayerAccountHolder) owner).accountHolder;
        	return player.getPlayer();
        }
    	
    	return null;
    }
    
    


}
