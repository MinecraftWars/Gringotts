package org.gestern.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

/**
 * Listens for chest creation and destruction events.
 * 
 * @author jast
 *
 */
public class AccountListener implements Listener {

	private DAO dao = DAO.getDao();
    @SuppressWarnings("unused")
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

        String line0 = event.getLine(0);
        AccountHolder chestOwner;
        if (line0.equalsIgnoreCase("[vault]")) {
        	if (!player.hasPermission("gringotts.createvault.player")) {
        		noPermission(player);
        		return;
        	}
            chestOwner = new PlayerAccountHolder(player);
        } else if (Dependency.dependency().factions != null && line0.equalsIgnoreCase("[faction vault]")) {
        	if (!player.hasPermission("gringotts.createvault.faction")) {
        		noPermission(player);
        		return;
        	}
        	
            FPlayer fplayer = FPlayers.i.get(player);
            Faction playerFaction = fplayer.getFaction();
            if (playerFaction == null) {
            	player.sendMessage("Cannot create faction vault: You are not in a faction.");
            	return;
            }
            chestOwner = new FactionAccountHolder(playerFaction);
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

    /**
     * Catches and handles breaking of the sign block of an account chest.
     * @param event
     */
    @EventHandler
    public void vaultBroken(BlockBreakEvent event) {
    	Block block = event.getBlock();
    	// only trigger on sign breaks
    	
    	if ( ! Util.isSignBlock(block) ) 
    		return;
    	
    	// don't bother if it isn't a valid vault marker sign
    	Sign sign = (Sign)block.getState();
    	if ( ! ("[vault]".equalsIgnoreCase(sign.getLine(0)) || "[faction vault]".equalsIgnoreCase(sign.getLine(0)) )) 
    		return;
    	
    	// TODO should be able to do this with a direct dao delete call
        Location loc = block.getLocation();
        for (AccountChest chest : dao.getChests()) {
        	if ( loc.equals(chest.sign.getBlock().getLocation()) ) {
        		chest.destroy();
        		Account account = chest.getAccount();
        		account.owner.sendMessage("Vault broken. New balance is " + account.balance());
        	}
        }
    }
    
    private static void noPermission(Player player) {
    	player.sendMessage("You do not have permission to create this vault.");
    }
}
