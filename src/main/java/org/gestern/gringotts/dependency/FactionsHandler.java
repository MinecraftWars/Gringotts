package org.gestern.gringotts.dependency;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.gestern.gringotts.Gringotts;
import org.gestern.gringotts.accountholder.AccountHolderProvider;
import org.gestern.gringotts.accountholder.FactionAccountHolder;
import org.gestern.gringotts.event.FactionsListener;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;

public class FactionsHandler implements DependencyHandler, AccountHolderProvider {
	
	private final P plugin;
	
	public FactionsHandler(P plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(new FactionsListener(), Gringotts.G);
		Gringotts.G.registerAccountHolderProvider("faction", this);
	}

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
	 * Get a FactionAccountHolder by id of the faction.
	 * @param id
	 * @return
	 */
	public FactionAccountHolder getAccountHolderById(String id) {
		Faction faction = Factions.i.get(id);
		return faction !=null? new FactionAccountHolder(faction) : null;
	}

	@Override
    public boolean enabled() {
	    return plugin!=null && plugin.isEnabled();
    }

	@Override
    public boolean exists() {
	    return plugin != null;
    }

	/**
	 * Get a FactionAccountHolder based on the name of the account.
	 * Valid ids for this method are either raw faction ids, or faction ids or tags prefixed with "faction-" 
	 * Only names beginning with "faction-" will be considered, and the rest of the string 
	 * can be either a faction id or a faction tag.
	 * @param name Name of the account.
	 * @return a FactionAccountHolder based on the name of the account, if a valid faction could be found. null otherwise.
	 */
	@Override
    public FactionAccountHolder getAccountHolder(String id) {
		
		// first try raw id
		FactionAccountHolder owner = getAccountHolderById(id);
		if (owner != null) return owner;
		
		// otherwise it's only a valid faction account name if owner starts with "faction-"
		if ( ! id.startsWith("faction-")) return null;
				
		// not sure, but somehow this is sometimes id, sometimes tag??
        String factionTag = id.substring(8);
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
