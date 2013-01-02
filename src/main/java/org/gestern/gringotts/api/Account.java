package org.gestern.gringotts.api;

public interface Account {

	/**
	 * Check if this account exists in the economy system.
	 * @return true if this account exists, false otherwise.
	 */
	boolean exists();
	
	/**
	 * Create this account, if it does not exist.
	 * The Account object returned as result of this method must return true to exists().
	 * @return Representation of this account after it has been created.
	 */
	Account create();
	
	/**
	 * Delete this account from the economy system.
	 * The Account object returned as result of this method must return true to exists().
	 * @return Representation of this account after it has been deleted.
	 */
	Account delete();
	
	/**
	 * Return the balance of this account.
	 * @return the balance of this account.
	 */
	double balance();
	
	/**
	 * Return whether this account has at least the specified amount.
	 * @param value amount to check
	 * @return whether this account has at least the specified amount.
	 */
	boolean has(double value);
	
	/**
	 * Set the balance of this account.
	 * Note: it is preferred to use the add and remove methods for any transactions that actually have 
	 * the goal of adding or removing a certain amount.
	 * @param newBalance the new balance of this account.
	 * @return result of setting the balance (success or failure type).
	 */
	TransactionResult setBalance(double newBalance);

	/**
	 * Add an amount to this account's balance.
	 * @param value amount to be added.
	 * @return result of adding (success or failure type)
	 */
	TransactionResult add(double value);
	
	/**
	 * Remove an amount from this account's balance.
	 * @param value amount to be removed
	 * @return result of removing (success or failure type)
	 */
	TransactionResult remove(double value);
	
	/**
	 * Send an amount to another account. If the transfer fails, both sender and recipient will 
	 * have unchanged account balance.
	 * @param value amount to be transferred
	 * @param recipient of the transfer
	 * @return result of this transaction. Can be SUCCESS if successful, 
	 * INSUFFICIENT_FUNDS if it failed because the sender lacks funds or
	 * INSUFFICIENT_SPACE if the recipient lacked space to receive the money. 
	 */
	TransactionResult sendTo(double value, Account recipient);
	
	/**
	 * Return the type of this account. Default account types are "player" and "bank".
	 * The method call Eco.custom(type,id) result in this account for parameters this.type() and this.id().
	 * @return the type of this account.
	 */
	String type();
	
	/**
	 * Return the id of this account. For players, this is the player name. For banks, this is the name of the bank.
	 * The method call Eco.custom(type,id) result in this account for parameters this.type() and this.id().
	 * @return
	 */
	String id();
	
	/**
	 * Send a message to the owner or owners of this account.
	 * Depending on the type of account, no player is the owner of an account. In this case, send the message to the console.
	 * @param message Message to send.
	 */
	void message(String message);

}
