package org.gestern.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class Account {

    @SuppressWarnings("unused")
	private final Logger log = Bukkit.getLogger();
    private final DAO dao = DAO.getDao(); 

    public final AccountHolder owner;

    public Account(AccountHolder owner) {
    	if (owner == null)
    		throw new IllegalArgumentException("owner parameter to Account constructor may not be null");
        this.owner = owner;
    }

    /**
     * Current balance of this account in cents
     * @return current balance of this account in cents
     */
    public long balanceCents() {
        long balance = 0;
        for (AccountChest chest : dao.getChests(this))
            balance += chest.balance();

        //Convert to total cents
        return balance*100 + dao.getCents(this);
    }

    /**
     * Current balance of this account.
     * @return
     */
    public double balance() {
        return Util.ToEmeralds( balanceCents() );
    }

    /**
     * Maximum capacity of this account in cents
     * @return maximum capacity of account in cents
     */
    public long capacityCents() {
        long capacity = 0;
        for (AccountChest chest: dao.getChests(this))
            capacity += chest.capacity();

        return capacity * 100;
    }

    /**
     * Maximum capacity of this account.
     * @return maximum capacity of account
     */
    public double capacity() {
        return Util.ToEmeralds(capacityCents());
    }

    /**
     * Add an amount in cents to this account if able to.
     * @param amount
     * @return Whether amount successfully added
     */
    public boolean addCents(long amount) {

        //Cannot add negative amount
        if(amount < 0)
            return false;

        //Is there space?
        if(balanceCents() + amount > capacityCents())
            return false;

        //Add the cents
        long cents = dao.getCents(this) + amount;

        //Convert excess cents into emeralds		
        long remainingEmeralds = 0;

        while(cents >= 100) {
            cents -= 100;
            remainingEmeralds += 1;
        }
        
        dao.storeCents(this, (int)cents);

        for (AccountChest chest : dao.getChests(this)) {
            remainingEmeralds -= chest.add(remainingEmeralds);
            if (remainingEmeralds <= 0) break;
        }

        return true;
    }

    /**
     * Add an amount to this account if able to.
     * @param amount
     * @return Whether amount successfully added
     */
    public boolean add(double amount) {
        return addCents(Util.ToCents(amount));
    }

    /**
     * Attempt to remove an amount in cents from this account. 
     * If the account contains less than the specified amount, returns false
     * @param amount
     * @return amount actually removed.
     */
    public boolean removeCents(long amount) {

        //Cannot remove negative amount
        if(amount < 0)
            return false;

        //Make sure we have enough to remove
        if(balanceCents() < amount)
            return false;

        //Remove the cents
        long cents = dao.getCents(this);
        cents -= amount;

        //Now lets get our amount of cents positive again, and count how many emeralds need removing
        long remainingEmeralds = 0;

        while(cents < 0) {
            cents += 100;
            remainingEmeralds += 1;
        }
        
        dao.storeCents(this, (int)cents);

        //Now remove the physical amount left
        for (AccountChest chest : dao.getChests(this)) {
            remainingEmeralds -= chest.remove(remainingEmeralds);
            if (remainingEmeralds <= 0) break;
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
        return removeCents(Util.ToCents(amount));
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

}
