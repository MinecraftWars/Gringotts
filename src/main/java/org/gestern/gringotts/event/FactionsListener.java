package org.gestern.gringotts.event;

import static org.gestern.gringotts.Permissions.createvault_faction;
import static org.gestern.gringotts.dependency.Dependency.DEP;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gestern.gringotts.accountholder.AccountHolder;

public class FactionsListener implements Listener {

	@EventHandler
	public void vaultCreated(PlayerVaultCreationEvent event) {
		// some listener already claimed this event
		if (event.isValid()) return;
		
		if (event.getType().equals("faction")) {
			Player player = event.getCause().getPlayer();
        	if (!createvault_faction.allowed(player)) {
        		player.sendMessage("You do not have permission to create a faction vault here.");
        		return;
        	}
        	
        	AccountHolder owner = DEP.factions.getFactionAccountHolder(player);
        	if (owner==null) {
        		player.sendMessage("Cannot create faction vault: You are not in a faction.");
            	return;
        	}
        	
        	event.setOwner(owner);
        	event.setValid(true);
        } 
	}
}
