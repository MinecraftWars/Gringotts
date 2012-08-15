package org.gestern.gringotts;

import java.util.Set;

import org.bukkit.Location;

public class DAO {

    /**
     * Save an account chest to database. 
     * @param chest
     * @throws GringottsStorageException when storage failed
     */
    public void storeAccountChest(AccountChest chest) {
    }
    
    /**
     * 
     * @param chest
     * @return
     */
    public boolean destroyAccountChest(AccountChest chest) {
        return false;
    }
    
    public boolean storeAccount(Account account) {
        return false;
    }
    
    public Account getAccount(String id) {
        return null;
    }

    
    public AccountChest getAccountChest(Location location) {
    	return null;
    }
    
    public Set<Account> getAccounts() {
        return null;
    }
    
    public Set<AccountChest> getChests() {
    	return null;
    }
    
    public static DAO getDao() {
    	return null;
    }
}
