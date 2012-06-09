package net.mcw.gringotts;

import java.util.HashSet;
import java.util.Set;

public class Account {
	
	private final Set<AccountChest> storage = new HashSet<AccountChest>();
	public final AccountHolder owner;
	
	public Account(AccountHolder owner) {
		this.owner = owner;
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
		for (AccountChest chest : storage) {
			remaining -= chest.add(remaining);
			if (remaining <= 0) break;
		}
		
		// TODO full: spawn items at random chest
		
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

}
