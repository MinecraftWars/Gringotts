package org.gestern.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Account {

    @SuppressWarnings("unused")
	private final Logger log = Bukkit.getLogger();
    private final DAO dao = DAO.getDao(); 

    public final AccountHolder owner;
    
    private final Util util = new Util();
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
        		balance += util.balanceInventory(player.getInventory());
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
     * Maximum capacity of this account in cents
     * @return maximum capacity of account in cents
     */
    long capacityCents() {
        long capacity = 0;
        
        if (config.usevaultContainer) {
	        for (AccountChest chest: dao.getChests(this))
	            capacity += chest.capacity();
        }
        
        Player player = playerOwner();
        if (player != null) {
        	if (player.hasPermission("gringotts.usevault.inventory"))
        		capacity += util.capacityInventory(player.getInventory());
        }

        return Util.toCents(capacity);
    }

    /**
     * Maximum capacity of this account.
     * @return maximum capacity of account
     */
    public double capacity() {
        return Util.toEmeralds(capacityCents());
    }

    /**
     * Add an amount in cents to this account if able to.
     * @param amount
     * @return Whether amount successfully added
     */
    boolean addCents(long amount) {

        //Cannot add negative amount
        if(amount < 0)
            return false;

        //Is there space?
        if(balanceCents() + amount > capacityCents())
            return false;

        //Add the cents
        long cents = amount;
        long remainingEmeralds = 0;
        if (config.currencyFractional) {
	        cents += dao.getCents(this);
	
	        //Convert excess cents into emeralds		
	        while(cents >= 100) {
	            cents -= 100;
	            remainingEmeralds += 1;
	        }
	        
	        dao.storeCents(this, (int)cents);
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
        		remainingEmeralds -= util.addToInventory(remainingEmeralds, player.getInventory());
        }
        
        return true;
    }

    /**
     * Add an amount to this account if able to.
     * @param amount
     * @return Whether amount successfully added
     */
    public boolean add(double amount) {
        return addCents(Util.toCents(amount));
    }

    /**
     * Attempt to remove an amount in cents from this account. 
     * If the account contains less than the specified amount, returns false
     * @param amount
     * @return amount actually removed.
     */
    boolean removeCents(long amount) {

        //Cannot remove negative amount
        if(amount < 0)
            return false;

        //Make sure we have enough to remove
        if(balanceCents() < amount)
            return false;

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
        	remainingEmeralds = cents/100;
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
        		remainingEmeralds -= util.removeFromInventory(remainingEmeralds, player.getInventory());
        }


        return true;
    }

    /**
     * Attempt to remove an amount from this account. 
     * If the account contains less than the specified amount, returns false
     * @param amount
     * @return amount actually removed.
     */
    public boolean remove(double amount) {
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
    public boolean transfer(double value, Account other) {

        //First try to deduct the amount from this account
        if(this.remove(value)) {
            //Okay, now lets send it to the other account
            if(other.add(value)) {
                //Success, yay
                return true;
            } else {
                //Oops, failed, better refund this account
                this.add(value);
            }
        }
        //We must have failed if execution made it here.
        return false;
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
