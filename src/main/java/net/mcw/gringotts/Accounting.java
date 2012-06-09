package net.mcw.gringotts;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Listens for chest creation and destruction events
 * @author jast
 *
 */
public class Accounting implements Listener {
	
	Logger log = Bukkit.getServer().getLogger();
	
	public Map<AccountHolder, Account> accounts = new HashMap<AccountHolder, Account>();
	public Map<AccountHolder, AccountChest> chests = new HashMap<AccountHolder, AccountChest>();

	/**
	 * Create an account chest by adding a sign marker over it.
	 * 
	 * @param event Event data.
	 */
	@EventHandler
	public void createAccountChest(SignChangeEvent event) {
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		
		if (lines.length == 0) return; //nothing to do!
		
		AccountHolder chestOwner;
		if (lines[0].equals("[vault]")) {
			chestOwner = new PlayerAccountHolder(player);
		} else if (lines[0].equals("[faction vault]")) {
			chestOwner = new FactionAccountHolder();
			// TODO faction vault creation
		} else return; // not for us!
		
		log.info("Vault created");
		
		Block chestBlock = event.getBlock().getRelative(BlockFace.DOWN);
		if (chestBlock.getType() == Material.CHEST) {
			
			Account account = getOrCreateAccount(chestOwner);
			
			// create account chest
			Chest chest = (Chest)chestBlock.getState();
			AccountChest accountChest = new AccountChest(chest, account);
			chests.put(chestOwner, accountChest);
			//FIXME this will probably not work correctly with double chests yet
		}
	}
	
	private Account getOrCreateAccount(AccountHolder owner) {
		Account account = accounts.get(owner);
		if (account == null) {
			account =  new Account(owner);
			accounts.put(owner,account);
		}
		
		return account;
	}
}
