package net.mcw.gringotts;

import java.util.HashSet;
import java.util.Set;

public class Account {
	
	private Set<Chest> storage = new HashSet<Chest>();
	private final AccountHolder owner;
	
	public Account(AccountHolder owner) {
		this.owner = owner;
	}
	
	/**
	 * Add a chest to the storage.
	 * @param chest chest to add to storage
	 * @return new amount of chests in storage
	 */
	public int addChest(Chest chest) {
		this.storage.add(chest);
		chest.setAccount(this);
		return storage.size();
	}

	public void removeChest(Chest chest) {
		storage.remove(chest);		
	}

	/**
	 * Current balance of this account.
	 * @return
	 */
	public long balance() {
		// TODO implement
		return 0;
	}
	
	/**
	 * Add an amount to this account. If the storage of this account is not large enough,
	 * extraneous value is dropped in front of random chests.
	 * @param value
	 * @return amount actually added
	 */
	public long add(long value) {
		
		long remaining = value;
		for (Chest chest : storage) {
			remaining -= chest.add(remaining);
			if (remaining <= 0) break;
		}
		
		// TODO full: spawn at random chests
		
		return remaining;
	}
	
	/**
	 * Attempt to remove an amount from this account. 
	 * If the account contains less than the specified amount, everything is removed
	 * and the actual removed amount returned.
	 * @param value
	 * @return amount actually removed.
	 */
	public long remove(int value) {
		// TODO:
		// remove items from storage by stack, count removed
		return 0;
	}
	
	/**
	 * Attempts to transfer an amount from this account to another account.
	 * @param value amount to transfer
	 * @param other Account to transfer to
	 * @return true if transfer was successful, false if funds on this account were insufficient.
	 */
	public boolean transfer(long value, Account other) {
		if (this.balance() >= value) {
			// TODO:
			// remove value from balance
			if(false) { // amount removed successfully
				other.add(value);
			} else {
				// rollback
			}
		}
		
		return false;
	}
}
