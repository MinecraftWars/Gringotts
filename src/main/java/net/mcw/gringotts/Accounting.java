package net.mcw.gringotts;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Listens for chest creation and destruction events
 * @author jast
 *
 */
public class Accounting implements Listener {
	
	private Logger log = Bukkit.getServer().getLogger();
	
	// TODO persistence of account chest data
	private Map<AccountHolder, Account> accounts = new HashMap<AccountHolder, Account>();
	private Map<Block, AccountChest> blockAccountChest = new HashMap<Block, AccountChest>();

	/**
	 * Create an account chest by adding a sign marker over it.
	 * 
	 * @param event Event data.
	 */
	@EventHandler
	public void createVault(SignChangeEvent event) {
		Player player = event.getPlayer();
		
		String line0 = event.getLine(0);
		AccountHolder chestOwner;
		if (line0.equals("[vault]")) {
			chestOwner = new PlayerAccountHolder(player);
			event.setLine(1, player.getName());
		} else if (line0.equals("[faction vault]")) {
			chestOwner = new FactionAccountHolder();
			// TODO faction vault creation
		} else return; // not for us!
		
		event.setLine(2, chestOwner.getName());
		
		Block signBlock = event.getBlock();
		Block chestBlock = signBlock.getRelative(BlockFace.DOWN);
		if (chestBlock.getType() == Material.CHEST) {
			
			Account account = getAccount(chestOwner);
			
			// create account chest
			Chest chest = (Chest)chestBlock.getState();
			AccountChest accountChest = new AccountChest(chest, (Sign)signBlock.getState(), account);
			
			// add to tracking
			blockAccountChest.put(signBlock, accountChest);
			blockAccountChest.put(chestBlock, accountChest);
			
			log.info("Vault created by " + player.getName());
			player.sendMessage("Created a vault for your account. New balance is " + account.balance());			
			
			//FIXME this will probably not work correctly with double chests yet
		}
	}
	
	@EventHandler
	public void vaultBroken(BlockBreakEvent event) {
		Block block = event.getBlock();
		AccountChest accountChest = blockAccountChest.get(block);
		if (accountChest != null) {
			Account account = accountChest.account;
			accountChest.destroy();
			blockAccountChest.remove(accountChest.chest);
			blockAccountChest.remove(accountChest.sign);
			
			account.owner.sendMessage("Vault broken. New balance is " + account.balance());
		}
	}
	
	public Account getAccount(AccountHolder owner) {
		Account account = accounts.get(owner);
		if (account == null) {
			account =  new Account(owner);
			accounts.put(owner,account);
		}
		
		return account;
	}
	
}
