package org.gestern.gringotts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Account implements ConfigurationSerializable {
	
	private final Logger log = Bukkit.getLogger();
	
	private final Set<AccountChest> storage;
	public final AccountHolder owner;
	
	//Stores any cents that cannot be stored physically
	private Long cents;
	
	/**
	 * Deserialization ctor.
	 * @param serialized
	 */
	@SuppressWarnings("unchecked")
	public Account(Map<String,Object> serialized) {
		log.info("[Gringotts] deserializing Account");
		
		this.storage = (Set<AccountChest>)serialized.get("storage");
		this.owner = (AccountHolder)serialized.get("owner");
		this.cents = new Long((Integer)serialized.get("cents"));
	}
	
	public Account(AccountHolder owner) {
		this.storage = new HashSet<AccountChest>();
		this.owner = owner;
		this.cents = new Long(0);
	}

	/**
	 * Add a chest to the storage.
	 * @param chest chest to add to storage
	 * @return new amount of chests in storage
	 */
	public int addChest(AccountChest chest) {
		this.storage.add(chest);
		return storage.size();
	}

	public void removeChest(AccountChest chest) {
		storage.remove(chest);		
	}
	
	/**
	 * Current balance of this account in cents
	 * @return
	 */
	public long balanceCents() {
		long balance = 0;
		for (AccountChest chest : storage)
			balance += chest.balance();
		
		//Convert to cents
		balance *= 100;
		
		return balance + cents;
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
		for (AccountChest chest: storage)
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
		this.cents += amount;
		
		//Convert excess cents into emeralds		
		long remainingEmeralds = 0;

		while(this.cents >= 100) {
			this.cents -= 100;
			remainingEmeralds += 1;
		}

		for (AccountChest chest : storage) {
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
		this.cents -= amount;
		
		//Now lets get our amount of cents positive again, and count how many emeralds need removing
		long remainingEmeralds = 0;

		while(this.cents < 0) {
			this.cents += 100;
			remainingEmeralds += 1;
		}
		
		//Now remove the physical amount left
		for (AccountChest chest : storage) {
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
	 * Return representation of current state of storage. 
	 * Changes to this Set will not affect the Account, 
	 * but changes to the contained elements will.
	 * @return representation of current state of storage.
	 */
	public Set<AccountChest> getStorage() {
		return new HashSet<AccountChest>(storage);
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

	public Map<String, Object> serialize() {
		Map<String, Object> serialized = new HashMap<String, Object>();
		
		serialized.put("storage",storage);
		serialized.put("owner", owner);
		serialized.put("cents", cents);
		return serialized;
	}

}
