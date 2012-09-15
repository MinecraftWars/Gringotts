package org.gestern.gringotts;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class AccountHolderFactory {
	
	@SuppressWarnings("unused")
	private final Logger log = Bukkit.getLogger();
	
	public AccountHolder getAccount(String owner) {
		return get(owner);
	}

	/**
	 * Get an account holder with automatically determined type, based on the owner's name.
	 * @param owner
	 * @return account holder for the given owner name, or null if none could be determined
	 */
    public AccountHolder get(String owner) {    	
        OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
        // if this player has ever played on the server, they are a legit account holder
        if (player.isOnline() || player.getLastPlayed() > 0) {
        	log.info("[Gringotts.AccountHolderFactory.debug] has been online: " + owner);
            return new PlayerAccountHolder(player);
        }

        if (owner.startsWith("faction-")) {
        	// not sure, but somehow this is sometimes id, sometimes tag??
            String factionTag = owner.substring(8);
            Faction faction;
            
            // try id first
            faction = Factions.i.get(factionTag);            
            // and then tag
            if (faction == null)
            	faction = Factions.i.getByTag(factionTag);
            
            if (faction != null) 
                return new FactionAccountHolder(faction);
            
        }

        return null;
    }
    
    /**
     * Get an account holder of known type.
     * @param type
     * @param owner
     * @return account holder of given type with given owner name, or null if none could be determined or type is not supported.
     */
    public AccountHolder get(String type, String owner) {
    	if (type.equals("player")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
            // if this player has ever played on the server, they are a legit account holder
            if (player.isOnline() || player.getLastPlayed() > 0) {
            	log.info("[Gringotts.AccountHolderFactory.debug] has been online: " + owner);
                return new PlayerAccountHolder(player);
            }
            else return null;
    	}
    	
    	if (type.equals("faction")) {
            if (Factions.i.exists(owner))
                return new FactionAccountHolder(Factions.i.get(owner));
            else return null;
    	}
    	
    	// no valid type
    	return null;
    }
}
