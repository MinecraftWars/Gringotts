package org.gestern.gringotts.data;

import org.gestern.gringotts.AccountChest;
import org.gestern.gringotts.GringottsAccount;
import org.gestern.gringotts.GringottsStorageException;
import org.gestern.gringotts.accountholder.AccountHolder;

import java.util.List;

public interface DAO {

    /**
     * Save an account chest to database. 
     * @param chest chest to save
     * @return true if chest was stored, false otherwise
     * @throws GringottsStorageException when storage failed
     */
    boolean storeAccountChest(AccountChest chest);

    /**
     * Remove an account chest from the datastore.
     * @param chest chest to remove
     * @return true if the chest was deleted, false if no chest was deleted.
     */
    boolean destroyAccountChest(AccountChest chest);

    /**
     * Store the given Account to DB.
     * @param account account to store
     * @return true if an account was stored, false if it already existed
     */
    boolean storeAccount(GringottsAccount account);

    /**
     * Return whether a given account owner has an account. 
     * @param accountHolder account holder to check
     * @return true if the account holder has an account associated with them
     */
    boolean hasAccount(AccountHolder accountHolder);

    /**
     * Get set of all chests registered with Gringotts.
     * If a stored chest turns out to be invalid, that chest may be removed from storage.
     * @return set of all chests registered with Gringotts
     */
    List<AccountChest> getChests();

    /**
     * Get all chests belonging to the given account.
     * If a stored chest turns out to be invalid, that chest is removed from storage.
     * @param account account to fetch chests for.
     * @return account to get chests for
     */
    List<AccountChest> getChests(GringottsAccount account);

    /**
     * Store an amount of cents to a given account.
     * @param account account to store amount to
     * @param amount amount to store to account
     * @return true if storing was successful, false otherwise.
     */
    boolean storeCents(GringottsAccount account, long amount);

    /**
     * Get the cents stored for a given account.
     * @param account account to query
     * @return amount of cents stored in the account, 0 if the account is not stored
     */
    long getCents(GringottsAccount account);

    /**
     * Delete an account and associated data from the storage.
     * @param acc account to delete
     */
    void deleteAccount(GringottsAccount acc);

    /**
     * Shutdown the database connection.
     */
    void shutdown();
}