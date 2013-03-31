package org.gestern.gringotts;

import static org.gestern.gringotts.Gringotts.G;

import java.util.Set;
import java.util.logging.Logger;

import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.data.DAO;

/**
 * Manages accounts.
 * 
 * @author jast
 *
 */
public class Accounting {

    private final Logger log = G.getLogger();

    /**
     * Get the account associated with an account holder.
     * If it was not yet stored in the data storage, it will be persisted.
     * @param owner account holder
     * @return account associated with an account holder
     */
    public GringottsAccount getAccount(AccountHolder owner) {
        GringottsAccount account = new GringottsAccount(owner);
        if (!G.dao.hasAccount(owner))  // TODO can we do this via idempotent store action instead?          
            G.dao.storeAccount(account);

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
    public boolean addChest(GringottsAccount account, AccountChest chest) {

        // TODO refactor to do a more intelligent/quick query
        Set<AccountChest> allChests = G.dao.getChests();

        // if there is an invalid stored chest on location of new chest, remove it from storage.
        if (allChests.contains(chest) && !chest.valid()) {
            log.info("removing orphaned vault: " + chest);
            G.dao.destroyAccountChest(chest);
            allChests.remove(chest);
        }

        if (chestConnected(chest, allChests) )
            return false;

        if ( ! G.dao.storeAccountChest(chest) )
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
