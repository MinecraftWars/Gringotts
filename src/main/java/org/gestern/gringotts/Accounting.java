package org.gestern.gringotts;

import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

/**
 * Manages accounts.
 * 
 * @author jast
 *
 */
public class Accounting {

	private final Logger log = Bukkit.getServer().getLogger(); 
    private final DAO dao = DAO.getDao();
    

    /**
     * Get the account associated with an account holder.
     * @param owner account holder
     * @return account associated with an account holder
     */
    public Account getAccount(AccountHolder owner) {
        Account account = dao.getAccount(owner);
        if (account == null) {
            account = new Account(owner);
            dao.storeAccount(account);
        }

        return account;
    }

    /**
     * Determine if a given accountchest would be connected to an accountchest already in storage.
     * Alas! need to call this every time we try to add an account chest, since chests can be added 
     * without us noticing ... 
     * @param chest
     * @return
     */
    private boolean chestConnected(AccountChest chest, Set<AccountChest> allChests) {
        for (AccountChest ac : allChests) {
        	if (ac.connected(chest))
        		return true;
        }
        return false;
    }

    /**
     * Associate an AccountChest with an Account. 
     * @param account
     * @param chest 
     * @return false if the specified AccountChest is already registered or would be connected to 
     * a registered chest. true if the association was successful. 
     * @throws GringottsStorageException when saving of account chest failed 
     */
    public boolean addChest(Account account, AccountChest chest) {
    	
    	Set<AccountChest> allChests = dao.getChests();
    	
    	// if there is an invalid stored chest on location of new chest, remove it from storage.
    	if (allChests.contains(chest) && !chest.valid()) {
    		log.info("[Gringotts][Accounting] removing orphaned vault: " + chest);
    		dao.destroyAccountChest(chest);
    		allChests.remove(chest);
    	}
    	
    	if (chestConnected(chest, allChests) )
            return false;

        if ( ! dao.storeAccountChest(chest) )
        	throw new GringottsStorageException("Could not save account chest: " + chest);
        return true;
    }

    /**
     * Determine whether a chest is valid in the game world.
     * @param chest
     * @return
     */
	public boolean validChest(AccountChest chest) {
		return false;
	}

}
