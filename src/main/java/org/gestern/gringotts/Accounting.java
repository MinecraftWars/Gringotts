package org.gestern.gringotts;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

/**
 * Manages accounts.
 * 
 * @author jast
 *
 */
public class Accounting {

    private final Logger log = Bukkit.getServer().getLogger(); 
    private final DAO dao = DAO.getDao();
    
    private final Map<AccountHolder, Account> accounts;
    private final Map<Block, AccountChest> blockAccountChest = new HashMap<Block, AccountChest>();
    private final Map<AccountChest, Account> accountChestAccount = new HashMap<AccountChest, Account>();

    public Accounting() {
        this.accounts = new HashMap<AccountHolder, Account>();
    }

    /**
     * Get the account associated with an account holder.
     * @param owner account holder
     * @return account associated with an account holder
     */
    public Account getAccount(AccountHolder owner) {
        Account account = accounts.get(owner);
        if (account == null) {
            account = new Account(owner);
            account.persist();
            accounts.put(owner,account);
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
        Location chestLocation = chest.chest.getLocation();
        for (AccountChest ac : dao.getChests()) {
            for (Chest c : ac.connectedChests()) {
                if (c.getLocation().equals(chestLocation))
                    return true;
            }
        }
        return false;
    }

    /**
     * Associate an AccountChest with an Account.
     * @param account
     * @param chest 
     * @param blocks
     * @return false if the specified AccountChest is already registered. 
     * 		true if the association was successful.  
     */
    public boolean addChest(Account account, AccountChest chest, Block... blocks) {
        if (accountChestAccount.containsKey(chest) || chestConnected(chest))
            return false;

        accountChestAccount.put(chest, account);
        for (Block block : blocks)
            blockAccountChest.put(block, chest);

        return true;
    }

    public void removeChest(AccountChest chest) {
        Account account = accountChestAccount.get(chest);
        if (account != null) {
            account.removeChest(chest);
            accountChestAccount.remove(chest);		
            for (Block block : chest.getBlocks())
                blockAccountChest.remove(block);
        } else {
            log.warning("[Gringotts] attempted to remove chest at " + chest.chest.getLocation() + " but it was not stored");
        }
    }

    public Account accountFor(AccountChest accountChest) {
        return accountChestAccount.get(accountChest);
    }

}
