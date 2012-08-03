package net.mcw.gringotts;

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
	
	//Stores any fractional amount that cannot be stored physically
	private double fraction;
	
	/**
	 * Deserialization ctor.
	 * @param serialized
	 */
	@SuppressWarnings("unchecked")
	public Account(Map<String,Object> serialized) {
		log.info("[Gringotts] deserializing Account");
		
		this.storage = (Set<AccountChest>)serialized.get("storage");
		this.owner = (AccountHolder)serialized.get("owner");
		this.fraction = (double)serialized.get("fraction");
	}
	
	public Account(AccountHolder owner) {
		this.storage = new HashSet<AccountChest>();
		this.owner = owner;
		this.fraction = 0;
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
	 * Current balance of this account.
	 * @return
	 */
	public double balance() {
		long balance = 0;
		for (AccountChest chest : storage)
			balance += chest.balance();
		
		return balance + fraction;
	}
	
	/**
	 * Maximum capacity of this account.
	 * @return maximum capacity of account
	 */
	public long capacity() {
		long capacity = 0;
		for (AccountChest chest: storage)
			capacity += chest.capacity();
		
		return capacity;
	}
	
	/**
	 * Add an amount to this account if able to.
	 * @param value
	 * @return Whether amount successfully added
	 */
	public boolean add(double value) {
		
		//Is there space?
		if(balance() + value > capacity())
			return false;
		
		//Get the fractional amount to add
		double fractionDiff = value % 1;
		
		//Get integral amount to add to chests
		long remaining = (long)(value - fractionDiff);
		
		//Add to the fractional amount
		this.fraction += fractionDiff;
		
		//Reduce the fractional amount down to < 1, add to physical amount to store as needed
		while(this.fraction > 1) {
			this.fraction -= 1;
			remaining += 1;
		}
		

		for (AccountChest chest : storage) {
			remaining -= chest.add(remaining);
			if (remaining <= 0) break;
		}
		
		return true;
	}
	
	/**
	 * Attempt to remove an amount from this account. 
	 * If the account contains less than the specified amount, returns false
	 * @param value
	 * @return amount actually removed.
	 */
	public boolean remove(double value) {
		
		//Make sure we have enough to remove
		if(balance() < value)
			return false;
		
		//Get the fractional amount to remove
		double fractionDiff = value % 1;
		
		//Work out how much we need to remove physically
		long remaining = (long)(value - fractionDiff);
		
		//First remove fractional amount
		this.fraction -= fractionDiff;
		
		//If we've removed more than our fractional amount, increase physical amount to remove and add to fraction
		while(this.fraction < 0) {
			this.fraction += 1;
			remaining += 1;
		}
		
		//Now remove the physical amount left
		for (AccountChest chest : storage) {
			remaining -= chest.remove(remaining);
			if (remaining <= 0) break;
		}
		
		return true;
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
		return serialized;
	}

}
