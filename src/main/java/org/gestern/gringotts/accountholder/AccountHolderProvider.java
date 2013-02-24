package org.gestern.gringotts.accountholder;


/**
 * Provides AccountHolder objects for a given id.
 * An AccountHolderProvider has its own internal mapping of ids to account holders.
 * For example, a Factions provider would return a FactionAccountHolder object when given the faction's id.
 * 
 * @author jast
 *
 */
public interface AccountHolderProvider {

    /**
     * Get the AccountHolder object mapped to the given id for this provider.
     * @param id id of account holder
     * @return account holder for id
     */
    public AccountHolder getAccountHolder(String id);
}
