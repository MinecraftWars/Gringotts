package org.gestern.gringotts;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Manages accounts.
 * 
 * @author jast
 *
 */
public class Accounting implements ConfigurationSerializable {
	
	private final Logger log = Bukkit.getServer().getLogger(); 
		
	private final Map<AccountHolder, Account> accounts;
	private final Map<Block, AccountChest> blockAccountChest = new HashMap<Block, AccountChest>();
	private final Map<AccountChest, Account> accountChestAccount = new HashMap<AccountChest, Account>();

	public Accounting() {
		this.accounts = new HashMap<AccountHolder, Account>();
	}
	
	/**
	 * Deserialization ctor.
	 * @param configMap
	 */
	@SuppressWarnings("unchecked")
	public Accounting(Map<String, Object> configMap) {
		log.info("[Gringotts] deserializing Accounting");
		
		Map<AccountHolder, Account> configAccounts = (Map<AccountHolder, Account>) configMap.get("accounts");
		this.accounts = configAccounts != null? configAccounts : new HashMap<AccountHolder, Account>();
				
		// reconstruct block -> chest mapping from accounts
		// reconstruct chest -> account mapping from accounts
		for (Account account : accounts.values())
			for (AccountChest chest : account.getStorage()) {
				accountChestAccount.put(chest, account);
				for (Block block : chest.getBlocks())
					blockAccountChest.put(block, chest);
			}
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
			accounts.put(owner,account);
		}
		
		return account;
	}
	
	/**
	 * Get the account chest associated with a block.
	 * @param block
	 * @return account chest associated with a block, or null if none
	 */
	public AccountChest chestAt(Block block) {
		return blockAccountChest.get(block);
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
	    for (AccountChest ac : accountChestAccount.keySet()) {
	        for (Chest c : ac.connectedChests()) {
	            if (c.getLocation().equals(chestLocation))
	                return true;
	        }
	    }
	    return false;
	}
	
	/**
	 * Associate an AccountChest with an Account.
	 * @param blocks
	 * @return false if the specified AccountChest is already registered. 
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

	public Map<String, Object> serialize() {
		Map<String, Object> configMap = new HashMap<String, Object>(2);
		configMap.put("accounts", accounts);
		return configMap;
	}

	public Account accountFor(AccountChest accountChest) {
		return accountChestAccount.get(accountChest);
	}
	
}
