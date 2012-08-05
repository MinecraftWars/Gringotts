package net.mcw.gringotts;

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

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

/**
 * Listens for chest creation and destruction events.
 * 
 * @author jast
 *
 */
public class AccountListener implements Listener {
	
	private Logger log = Bukkit.getServer().getLogger();
	private final Accounting accounting;

	public AccountListener(Gringotts gringotts) {
		this.accounting = gringotts.accounting;
	}

	/**
	 * Create an account chest by adding a sign marker over it.
	 * 
	 * @param event Event data.
	 */
	@EventHandler
	public void createVault(SignChangeEvent event) {
		Player player = event.getPlayer();
		FPlayer fplayer = FPlayers.i.get(player);
		
		String line0 = event.getLine(0);
		AccountHolder chestOwner;
		if (line0.equals("[vault]")) {
			chestOwner = new PlayerAccountHolder(player);
		} else if (line0.equals("[faction vault]")) {
			chestOwner = new FactionAccountHolder(fplayer);
		} else return; // not for us!
		
		Block signBlock = event.getBlock();
		Block chestBlock = signBlock.getRelative(BlockFace.DOWN);
		if (chestBlock.getType() == Material.CHEST) {
			event.setLine(2, chestOwner.getName());
			Account account = accounting.getAccount(chestOwner);
			
			// create account chest
			Chest chest = (Chest)chestBlock.getState();
			AccountChest accountChest = new AccountChest(chest, (Sign)signBlock.getState());
			account.addChest(accountChest);
			
			// add to tracking
			accounting.addChest(account, accountChest, signBlock, chestBlock);;
			
			log.info("Vault created by " + player.getName());
			player.sendMessage("Created a vault for your account. New balance is " + account.balance());			
		}
	}
	
	@EventHandler
	public void vaultBroken(BlockBreakEvent event) {
		Block block = event.getBlock();
		AccountChest accountChest = accounting.chestAt(block);
		if (accountChest != null) {
//			Account account = accountChest.account;
			Account account = accounting.accountFor(accountChest);
			accountChest.destroy();
			accounting.removeChest(accountChest);
			
			account.owner.sendMessage("Vault broken. New balance is " + account.balance());
		}
	}
}
