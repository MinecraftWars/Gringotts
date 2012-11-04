package org.gestern.gringotts.dependency;

import org.bukkit.entity.Player;
import org.gestern.gringotts.accountholder.FactionAccountHolder;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class FactionsHandler implements DependencyHandler {

	/**
	 * Get a FactionAccountHolder for the faction of which player is a member, if any.
	 * @param player
	 * @return FactionAccountHolder for the faction of which player is a member, if any. null otherwise.
	 */
	public FactionAccountHolder getFactionAccountHolder(Player player) {

        FPlayer fplayer = FPlayers.i.get(player);
        Faction playerFaction = fplayer.getFaction();
        return playerFaction != null? new FactionAccountHolder(playerFaction) : null;
	}
	
	/**
	 * Get a FactionAccountHolder based on the name of the account. 
	 * Only names beginning with "faction-" will be considered, and the rest of the string 
	 * can be either a faction id or a faction tag.
	 * @param name Name of the account.
	 * @return a FactionAccountHolder based on the name of the account, if a valid faction could be found. null otherwise.
	 */
	public FactionAccountHolder getAccountHolderByName(String name) {
    	
		// only a valid faction account name if owner starts with "faction-"
		if ( ! name.startsWith("faction-")) return null;
				
		// not sure, but somehow this is sometimes id, sometimes tag??
        String factionTag = name.substring(8);
        Faction faction;
        
        // try id first
        faction = Factions.i.get(factionTag);            
        // and then tag
        if (faction == null)
        	faction = Factions.i.getByTag(factionTag);
        
        if (faction != null) 
            return new FactionAccountHolder(faction);
        
        return null;
	}

}
