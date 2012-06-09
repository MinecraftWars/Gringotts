package net.mcw.gringotts;



/**
 * An account holder. Can be a player or a faction.
 * @author jast
 *
 */
abstract public class AccountHolder {

	/** 
	 * Return name of the account holder.
	 * 
	 * @return name of the account holder
	 */
	abstract public String getName();
	
	/** Send message to the account holder. */
	abstract public void sendMessage(String message);

	abstract public int hashCode();
	abstract public boolean equals(Object other);
}
