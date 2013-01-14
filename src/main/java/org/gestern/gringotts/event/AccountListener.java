package org.gestern.gringotts.event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.gestern.gringotts.AccountChest;

import static org.gestern.gringotts.Configuration.CONF;

/**
 * Listens for chest creation and destruction events.
 * 
 * @author jast
 *
 */
public class AccountListener implements Listener { 

    private final Pattern vaultPattern = Pattern.compile(CONF.vaultPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    
    /**
     * Create an account chest by adding a sign marker over it.
     * 
     * @param event Event data.
     */
    @EventHandler
    public void createVault(SignChangeEvent event) {
        String line0 = event.getLine(0);
        
        Matcher match = vaultPattern.matcher(line0);
        
        // consider only signs with proper formatting
        if (match.matches()) {
        	String type = match.group(1).toLowerCase();

        	// default vault is player
        	if (type.isEmpty()) type = "player";

        	// is sign attached to a valid vault container?
        	Block signBlock = event.getBlock();
        	org.bukkit.material.Sign sign = (org.bukkit.material.Sign)signBlock.getState().getData();
            BlockFace attached = sign.getAttachedFace();
            
            // allow either the block sign is attached to or the block below the sign as chest block. Prefer attached block.
            // TODO: Make container checking less redundant
            Block blockAttached = signBlock.getRelative(attached);
            Block blockBelow = signBlock.getRelative(BlockFace.DOWN);
            if (! AccountChest.validContainer(blockAttached.getType()) &&
            	! AccountChest.validContainer(blockBelow.getType()))
            	return; // no valid container, ignore
                        
            // we made it this far, throw the event to manage vault creation
        	VaultCreationEvent creation = new PlayerVaultCreationEvent(type, event);
        	Bukkit.getServer().getPluginManager().callEvent(creation);
        }
    }
}
