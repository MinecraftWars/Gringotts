package org.gestern.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;

/**
 * Manages accounts.
 * 
 * @author jast
 *
 */
public class Accounting {

    @SuppressWarnings("unused")
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
    private boolean chestConnected(AccountChest chest) {
        for (AccountChest ac : dao.getChests()) {
        	if (ac.connected(chest))
        		return true;
        }
        return false;
    }

    /**
     * Associate an AccountChest with an Account.
     * @param account
     * @param chest 
     * @return false if the specified AccountChest is already registered or would be connected to a registered chest. 
     * 		true if the association was successful.  
     */
    public boolean addChest(Account account, AccountChest chest) {
    	
        if (chestConnected(chest))
            return false;

        dao.storeAccountChest(chest);
        return true;
    }

}
