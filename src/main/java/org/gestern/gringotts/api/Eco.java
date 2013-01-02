package org.gestern.gringotts.api;

public interface Eco {

	/**
	 * Access a player account for the player with given name.
	 * The representation of the money of a player account may be in-game items or virtual.
	 * @param name The name of the player owning the account.
	 * @return The player account representation
	 */
	Account player(String name);
	
	/**
	 * Access a bank account with given name.
	 * The representation of the money in a bank account may be in-game items or virtual.
	 * Support for this method is optional.
	 * @param name The name or id of the bank.
	 * @return The bank account representation.
	 */
	Account bank(String name);
	
	/**
	 * Access custom account type. Implementors must support this method, but may choose to implement via another account type internally.
	 * @param type
	 * @param id
	 * @return
	 */
	Account custom(String type, String id);
	
	/**
	 * Access a Factions faction account with the given id.
	 * @param id
	 * @return
	 */
	Account faction(String id);
	
	/**
	 * Access a Towny town account with the given id.
	 * @param id/name of a Towny town
	 * @return account for a Towny town
	 */
	Account town(String id);
	
	/**
	 * Access a Towny nation account with the given id.
	 * @param id id/name of a Towny nation
	 * @return account for a Towny nation with the given id.
	 */
	Account nation(String id);
	
	/**
	 * The currency for this Economy.
	 * @return
	 */
	Currency currency();
	
	/**
	 * Return whether this economy supports banks.
	 * @return true if this economy has bank support, false otherwise.
	 */
	boolean supportsBanks();
}
