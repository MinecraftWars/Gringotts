package org.gestern.gringotts;

import static org.gestern.gringotts.Permissions.createvault_faction;
import static org.gestern.gringotts.Permissions.createvault_nation;
import static org.gestern.gringotts.Permissions.createvault_player;
import static org.gestern.gringotts.Permissions.createvault_town;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.gestern.gringotts.accountholder.AccountHolder;
import org.gestern.gringotts.accountholder.PlayerAccountHolder;
import org.gestern.gringotts.dependency.Dependency;
import org.gestern.gringotts.dependency.FactionsHandler;
import org.gestern.gringotts.dependency.TownyHandler;

/**
 * Listens for chest creation and destruction events.
 * 
 * @author jast
 *
 */
public class AccountListener implements Listener { 

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

        String line0 = event.getLine(0);
        AccountHolder chestOwner;
        if (line0.equalsIgnoreCase("[vault]")) {
        	if (!createvault_player.allowed(player)) {
        		noPermission(player);
        		return;
        	}
            chestOwner = new PlayerAccountHolder(player);
        } else if (Dependency.D.factions != null && line0.equalsIgnoreCase("[faction vault]")) {
        	if (!createvault_faction.allowed(player)) {
        		noPermission(player);
        		return;
        	}
        	
        	FactionsHandler handler = new FactionsHandler();
        	AccountHolder holder = handler.getFactionAccountHolder(player);
        	if (holder==null) {
        		player.sendMessage("Cannot create faction vault: You are not in a faction.");
            	return;
        	}
            chestOwner = holder;
        } else if (Dependency.D.towny != null  && line0.equalsIgnoreCase("[town vault]")) {
        	if (!createvault_town.allowed(player)) {
        		noPermission(player);
        		return;
        	}
        	
        	TownyHandler handler = new TownyHandler();
        	chestOwner = handler.getTownAccountHolder(player);
        	if (chestOwner == null) {
        		player.sendMessage("Cannot create town vault: You are not resident of a town.");
				return;
        	}
        	
        } else if (Dependency.D.towny != null  && line0.equalsIgnoreCase("[nation vault]")) {
        	if (!createvault_nation.allowed(player)) {
        		noPermission(player);
        		return;
        	}
        	
        	TownyHandler handler = new TownyHandler();
        	chestOwner = handler.getNationAccountHolder(player);
        	if (chestOwner == null) {
        		player.sendMessage("Cannot create nation vault.");
				return;
        	}
        	
        } else return; // not for us!

        Block signBlock = event.getBlock();
        Block chestBlock = signBlock.getRelative(BlockFace.DOWN);
        if (AccountChest.validContainer(chestBlock.getType())) {
            Account account = accounting.getAccount(chestOwner);
            // create account chest
            AccountChest accountChest = new AccountChest((Sign)signBlock.getState(), account);
            
            // check for existence / add to tracking
            if (accounting.addChest(account, accountChest)) {
                event.setLine(2, chestOwner.getName());
                player.sendMessage("Created a vault for your account.");

            } else {
                event.setCancelled(true);
                player.sendMessage("Failed to create vault.");
            }
        }
    }

    private static void noPermission(Player player) {
    	player.sendMessage("You do not have permission to create this vault.");
    }
}
