package net.mcw.gringotts;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;

/**
 * Manages accounts.
 * 
 * @author jast
 *
 */
public class Accounting {
		
	// TODO persistence of account chest data
	private Map<AccountHolder, Account> accounts = new HashMap<AccountHolder, Account>();
	private Map<Block, AccountChest> blockAccountChest = new HashMap<Block, AccountChest>();

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
	 * Associa
	 * @param blocks
	 */
	public void addChest(AccountChest chest, Block... blocks) {
		for (Block block : blocks)
			blockAccountChest.put(block, chest);
	}

	public void removeChest(Block... blocks) {
		for (Block block : blocks)
			blockAccountChest.remove(block);
	}
}
