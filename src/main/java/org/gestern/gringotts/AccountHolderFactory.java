package org.gestern.gringotts;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.massivecraft.factions.Factions;

public class AccountHolderFactory {

	/**
	 * Get an account holder with automatically determined type, based on the owner's name.
	 * @param owner
	 * @return account holder for the given owner name, or null if none could be determined
	 */
    public AccountHolder get(String owner) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
        // if this player has ever played on the server, they are a legit account holder
        if (player.getLastPlayed() > 0)
            return new PlayerAccountHolder(player);

        if (owner.startsWith("faction-")) {
            String factionId = owner.substring(8);
            if (Factions.i.exists(factionId))
                return new FactionAccountHolder(Factions.i.get(factionId));
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
            if (player.getLastPlayed() > 0)
                return new PlayerAccountHolder(player);
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
