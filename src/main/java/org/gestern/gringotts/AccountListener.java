package org.gestern.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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

/**
 * Listens for chest creation and destruction events.
 * 
 * @author jast
 *
 */
public class AccountListener implements Listener {

	private DAO dao = DAO.getDao();
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
        if (line0.equals("[vault]")) {
            chestOwner = new PlayerAccountHolder(player);
        } else if (line0.equals("[faction vault]")) {
            FPlayer fplayer = FPlayers.i.get(player);
            chestOwner = new FactionAccountHolder(fplayer.getFaction());
        } else return; // not for us!

        Block signBlock = event.getBlock();
        Block chestBlock = signBlock.getRelative(BlockFace.DOWN);
        if (chestBlock.getType() == Material.CHEST) {
            event.setLine(2, chestOwner.getName());
            Account account = new Account(chestOwner);
            // create account chest
            AccountChest accountChest = new AccountChest((Sign)signBlock.getState(), account);

            // check for existence / add to tracking
            if (accounting.addChest(account, accountChest)) {
                account.addChest(accountChest);
                log.info("Vault created by " + player.getName());
                player.sendMessage("Created a vault for your account. New balance is " + account.balance());
            } else {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Catches and handles breaking of the sign block of an account chest.
     * @param event
     */
    @EventHandler
    public void vaultBroken(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        for (AccountChest chest : dao.getChests()) {
        	if ( loc.equals(chest.sign.getBlock().getLocation()) ) {
        		chest.destroy();
        		Account account = chest.getAccount();
        		account.owner.sendMessage("Vault broken. New balance is " + account.balance());
        	}
        }
    }
}
