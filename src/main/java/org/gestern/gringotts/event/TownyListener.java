package org.gestern.gringotts.event;

import static org.gestern.gringotts.Permissions.createvault_nation;
import static org.gestern.gringotts.Permissions.createvault_town;
import static org.gestern.gringotts.dependency.Dependency.DEP;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gestern.gringotts.accountholder.AccountHolder;

public class TownyListener implements Listener {

	@EventHandler
	public void vaultCreated(PlayerVaultCreationEvent event) {
		// some listener already claimed this event
		if (event.isValid()) return;
		
		if (! DEP.towny.enabled()) return;
		
		Player player = event.getCause().getPlayer();
		AccountHolder owner = null;
		if (event.getType().equals("town")) {
        	if (!createvault_town.allowed(player)) {
        		player.sendMessage("You do not have permission to create town vaults here.");
        		return;
        	}
        	
        	owner = DEP.towny.getTownAccountHolder(player);
        	if (owner == null) {
        		player.sendMessage("Cannot create town vault: You are not resident of a town.");
				return;
        	}
        	
        } else if (event.getType().equals("nation")) {
        	if (!createvault_nation.allowed(player)) {
        		player.sendMessage("You do not have permission to create nation vaults here.");
        		return;
        	}
        	
        	owner = DEP.towny.getNationAccountHolder(player);
        	if (owner == null) {
        		player.sendMessage("Cannot create nation vault: You do not belong to a nation.");
				return;
        	}
        }
		
		event.setOwner(owner);
    	event.setValid(true);
	}
}
