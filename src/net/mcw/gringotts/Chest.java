package net.mcw.gringotts;

/**
 * Represents a storage unit for an account.
 * 
 * @author jast
 *
 */
public class Chest {
	
	/** Account that this chest belongs to. */
	private Account account;
	
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * Return balance of this chest.
	 * @return balance of this chest
	 */
	public long balance() {
		// TODO
		return 0;
	}
	
	/**
	 * Attempts to add given amount to this chest. 
	 * If the amount is larger than available space, the space is filled and the actually
	 * added amount returned.
	 * @return amount actually added
	 */
	public long add(long value) {
		// TODO:
		// add items stack by stack, count progress
		return 0;
	}
	
	/**
	 * Attempts to remove given amount from this chest.
	 * If the amount is larger than available items, everything is removed and the number of
	 * removed items returned.
	 * @param value
	 * @return
	 */
	public long remove(long value) {
		long count = 0;
		// TODO: remove items stack by stack, count progress
		return count;
	}
	
	/**
	 * Triggered on destruction of physical chest.
	 */
	private void destroy() {
		account.removeChest(this);
		// TODO implement 
	}
}
