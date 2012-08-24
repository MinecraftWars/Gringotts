package org.gestern.gringotts;

/**
 * Any implementor of this class supports some form of 
 * database persistence via the DAO object.
 * 
 * @author jast
 *
 */
public interface Persisted {
	
	/**
	 * Persist the object to database.
	 * @return true iff persistence was successful, false otherwise
	 */
	boolean persist();
}
